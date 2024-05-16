package com.oviva.spicegen.generator.internal;

import static com.oviva.spicegen.generator.utils.TextUtils.toPascalCase;

import com.oviva.spicegen.generator.SpiceDbClientGenerator;
import com.oviva.spicegen.generator.utils.TextUtils;
import com.oviva.spicegen.model.*;
import com.squareup.javapoet.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import javax.lang.model.element.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiceDbClientGeneratorImpl implements SpiceDbClientGenerator {

  private static Logger logger = LoggerFactory.getLogger(SpiceDbClientGeneratorImpl.class);
  //  private final Options options;
  //
  //  public SpiceDbClientGeneratorImpl(Options options) {
  //    this.options = options;
  //  }

  private static final String sourceDirectory = "./out/src/main/java";
  private static final String sourcePackageName = "com.oviva.spicegen";

  private TypeSpecStore typeSpecStore;

  @Override
  public void generate(Schema spec) {
    typeSpecStore = new TypeSpecStore();
    generateConstants(spec);
    generateUpdateRelationshipClass();
    generateRefs(spec);
  }

  private void generateConstants(Schema spec) {

    TypeSpec.Builder constants =
        TypeSpec.classBuilder("SchemaConstants")
            .addModifiers(Modifier.PUBLIC)
            .addField(
                FieldSpec.builder(String.class, "PLATFORM_ID")
                    .initializer("\"1\"")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .build());

    for (ObjectDefinition definition : spec.definitions()) {
      FieldSpec namespace =
          FieldSpec.builder(String.class, "NAMESPACE_" + definition.name().toUpperCase())
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .initializer("\"" + definition.name() + "\"")
              .build();
      constants.addField(namespace);

      for (Permission permission : definition.permissions()) {
        FieldSpec permissionField =
            FieldSpec.builder(
                    String.class,
                    "PERMISSION_"
                        + definition.name().toUpperCase()
                        + "_"
                        + permission.name().toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + permission.name() + "\"")
                .build();
        constants.addField(permissionField);
      }

      for (Relation relations : definition.relations()) {
        FieldSpec relationshipField =
            FieldSpec.builder(
                    String.class,
                    "RELATIONSHIP_"
                        + definition.name().toUpperCase()
                        + "_"
                        + relations.name().toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + relations.name() + "\"")
                .build();
        constants.addField(relationshipField);
      }
    }

    var constantsClass = constants.build();
    if (typeSpecStore.has(constantsClass.name)) {
      return;
    }
    typeSpecStore.put(constantsClass);
    writeSource(constantsClass, "");
  }

  private void generateRefs(Schema spec) {
    var objectRef = createObjectRef();
    var subjectRef = createSubjectRef();

    for (ObjectDefinition definition : spec.definitions()) {
      var className = TextUtils.capitalize(TextUtils.toCamelCase(definition.name())) + "Ref";
      var typedRef = TypeSpec.classBuilder(className).build();
      if (typeSpecStore.has(typedRef.name)) {
        return;
      }
      typeSpecStore.put(typedRef);

      var typedRefBuilder =
          typedRef.toBuilder()
              .addSuperinterface(ClassName.bestGuess("ObjectRef"))
              .addModifiers(Modifier.PUBLIC)
              .addField(
                  FieldSpec.builder(
                          String.class, "kind", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                      .initializer("\"" + definition.name() + "\"")
                      .build())
              .addField(String.class, "id", Modifier.PRIVATE)
              .addMethod(
                  MethodSpec.constructorBuilder()
                      .addModifiers(Modifier.PRIVATE)
                      .addParameter(String.class, "id")
                      .addStatement("this.id = id")
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("kind")
                      .addModifiers(Modifier.PUBLIC)
                      .returns(String.class)
                      .addCode(CodeBlock.builder().addStatement("return kind").build())
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("id")
                      .addModifiers(Modifier.PUBLIC)
                      .returns(String.class)
                      .addCode(CodeBlock.builder().addStatement("return id").build())
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("ofUuid")
                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                      .returns(ClassName.bestGuess(className))
                      .addParameter(UUID.class, "id")
                      .addCode(
                          "if (id == null) {\n"
                              + " throw new IllegalArgumentException(\"id must not be null\");\n"
                              + "}\n"
                              + "return new $T(id.toString());",
                          ClassName.bestGuess(className))
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("ofLong")
                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                      .returns(ClassName.bestGuess(className))
                      .addParameter(TypeName.LONG, "id")
                      .addCode("return new $T(String.valueOf(id));", ClassName.bestGuess(className))
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("of")
                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                      .returns(ClassName.bestGuess(className))
                      .addParameter(String.class, "id")
                      .addCode(
                          "if (id == null) {\n"
                              + " throw new IllegalArgumentException(\"id must not be null\");\n"
                              + "}\n"
                              + "return new $T(id);",
                          ClassName.bestGuess(className))
                      .build());

      addUpdateMethods(typedRefBuilder, definition);

      typedRef = typedRefBuilder.build();
      writeSource(typedRef, ".refs");
    }
  }

  private void addUpdateMethods(TypeSpec.Builder typeRefBuilder, ObjectDefinition definition) {

    for (Relation relation : definition.relations()) {

      var relationCamelCase = TextUtils.toPascalCase(relation.name());

      for (ObjectTypeRef allowedObject : relation.allowedObjects()) {
        if (allowedObject.relationship() != null) {
          logger.atInfo().log(
              "skipping update util for {}.{}#{}, can't deal with relationships on allowed objects",
              definition.name(),
              allowedObject.typeName(),
              allowedObject.relationship());
          continue;
        }

        // add create
        var createMethod =
            "create" + relationCamelCase + TextUtils.toPascalCase(allowedObject.typeName());

        // TODO magic ref
        var typeRefName = toPascalCase(allowedObject.typeName()) + "Ref";

        typeRefBuilder.addMethod(
            MethodSpec.methodBuilder(createMethod)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("com.oviva.spicegen.refs", typeRefName), "ref")
                .returns(ClassName.bestGuess("UpdateRelationship"))
                .addCode(
                    """
                                if ($L == null) {
                                 throw new IllegalArgumentException("ref must not be null");
                                }
                                return $T.ofUpdate(this, $S, $L);
                                """,
                    "ref",
                    ClassName.bestGuess("UpdateRelationship"),
                    relation.name(),
                    "ref")
                .build());

        var deleteMethod =
            "delete" + relationCamelCase + TextUtils.toPascalCase(allowedObject.typeName());

        typeRefBuilder.addMethod(
            MethodSpec.methodBuilder(deleteMethod)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("com.oviva.spicegen.refs", typeRefName), "ref")
                .returns(ClassName.bestGuess("UpdateRelationship"))
                .addCode(
                    """
                                            if ($L == null) {
                                             throw new IllegalArgumentException("ref must not be null");
                                            }
                                            return $T.ofDelete(this, $S, $L);
                                            """,
                    "ref",
                    ClassName.bestGuess("UpdateRelationship"),
                    relation.name(),
                    "ref")
                .build());
      }
    }
  }

  private TypeSpec createObjectRef() {

    var objectRef = TypeSpec.interfaceBuilder("ObjectRef").build();

    if (typeSpecStore.has(objectRef.name)) {
      return typeSpecStore.get(objectRef.name);
    }

    typeSpecStore.put(objectRef);
    objectRef =
        objectRef.toBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addMethod(
                MethodSpec.methodBuilder("kind")
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(String.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("id")
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(String.class)
                    .build())
            .build();

    writeSource(objectRef, ".refs");
    return objectRef;
  }

  private TypeSpec createSubjectRef() {

    var subjectRef = TypeSpec.interfaceBuilder("SubjectRef").build();

    if (typeSpecStore.has(subjectRef.name)) {
      return typeSpecStore.get(subjectRef.name);
    }

    typeSpecStore.put(subjectRef);
    subjectRef =
        subjectRef.toBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addMethod(
                MethodSpec.methodBuilder("kind")
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(String.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("id")
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(String.class)
                    .build())
            // TODO - extension
            //        .addMethod(
            //            MethodSpec.methodBuilder("optionalRelation")
            //                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
            //                .returns(String.class)
            //                .build())
            .build();

    writeSource(subjectRef, ".refs");
    return subjectRef;
  }

  private void generateUpdateRelationshipClass() {

    var operationTypeSpec =
        TypeSpec.enumBuilder("Operation")
            .addModifiers(Modifier.PUBLIC)
            .addEnumConstant("UPDATE")
            .addEnumConstant("DELETE")
            .build();

    var objectRefType = ClassName.bestGuess("ObjectRef");
    var subjectRefType = ClassName.bestGuess("SubjectRef");
    var operationType = ClassName.bestGuess(operationTypeSpec.name);

    var objectRefSubjectTypeSpec =
        TypeSpec.classBuilder("ObjectRefSubject")
            .addSuperinterface(subjectRefType)
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addField(objectRefType, "subject", Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(objectRefType, "subject")
                    .addCode(
                        """
                    this.subject = subject;
                    """)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("kind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode("return subject.kind();")
                    .returns(String.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("id")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode("return subject.id();")
                    .returns(String.class)
                    .build())
            .build();
    var objectRefSubjectType = ClassName.bestGuess("ObjectRefSubject");

    var self = TypeSpec.classBuilder(ClassName.bestGuess("UpdateRelationship")).build();
    var updateRelationshipTypeSpec =
        self.toBuilder()
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            // fields
            .addField(objectRefType, "resource", Modifier.PRIVATE, Modifier.FINAL)
            .addField(ClassName.get(String.class), "relation", Modifier.PRIVATE, Modifier.FINAL)
            .addField(subjectRefType, "subject", Modifier.PRIVATE, Modifier.FINAL)
            .addField(operationType, "operation", Modifier.PRIVATE, Modifier.FINAL)

            // constructor
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(objectRefType, "resource")
                    .addParameter(ClassName.get(String.class), "relation")
                    .addParameter(subjectRefType, "subject")
                    .addParameter(operationType, "operation")
                    .addCode(
                        """
                         this.resource = resource;
                         this.relation = relation;
                         this.subject = subject;
                         this.operation = operation;
                     """)
                    .build())

            // factory methods
            .addMethod(
                MethodSpec.methodBuilder("ofUpdate")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(objectRefType, "resource")
                    .addParameter(ClassName.get(String.class), "relation")
                    .addParameter(objectRefType, "subject")
                    .addCode(
                        """
                      return new UpdateRelationship(resource, relation, new $T(subject), $T.UPDATE);
                      """,
                        objectRefSubjectType,
                        operationType)
                    .returns(ClassName.bestGuess(self.name))
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("ofDelete")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(objectRefType, "resource")
                    .addParameter(ClassName.get(String.class), "relation")
                    .addParameter(objectRefType, "subject")
                    .addCode(
                        """
                        return new UpdateRelationship(resource, relation, new $T(subject), $T.DELETE);
                        """,
                        objectRefSubjectType,
                        operationType)
                    .returns(ClassName.bestGuess(self.name))
                    .build())
            // getters
            .addMethod(
                MethodSpec.methodBuilder("subject")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                        """
                        return subject;
                        """)
                    .returns(subjectRefType)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("resource")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                        """
                        return resource;
                        """)
                    .returns(objectRefType)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("relation")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                        """
                        return relation;
                        """)
                    .returns(ClassName.get(String.class))
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("operation")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                        """
                        return operation;
                        """)
                    .returns(operationType)
                    .build())

            // toString
            .addMethod(
                MethodSpec.methodBuilder("toString")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addCode(
                        """
                    var res = resource != null ? resource.toString() : "";
                    var rel = relation != null ? relation : "";
                    var sub = subject != null ? subject.toString() : "";
                    return operation.name() + "(" + res + "#" + rel + "@" + sub + ")";
                    """)
                    .returns(ClassName.get(String.class))
                    .build())

            // equals & hashCode
            .addMethod(
                MethodSpec.methodBuilder("equals")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ClassName.get(Object.class), "o")
                    .addCode(
                        """
                    if (this == o) {
                      return true;
                    }
                    if (o == null || getClass() != o.getClass()) {
                      return false;
                    }
                    var that = (UpdateRelationship) o;
                    return $T.equals(resource, that.resource)
                        && $T.equals(relation, that.relation)
                        && $T.equals(subject, that.subject)
                        && operation == that.operation;
                    """,
                        ClassName.get(Objects.class),
                        ClassName.get(Objects.class),
                        ClassName.get(Objects.class))
                    .returns(TypeName.BOOLEAN)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("hashCode")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addCode(
                        """
                      return $T.hash(resource, relation, subject, operation);
                      """,
                        ClassName.get(Objects.class))
                    .returns(TypeName.INT)
                    .build())

            // inner types
            .addType(operationTypeSpec)
            .addType(objectRefSubjectTypeSpec)
            .build();

    writeSource(updateRelationshipTypeSpec, ".refs");
  }

  private void writeSource(TypeSpec typeSpec, String subpackage) {
    var outputDirectory = sourceDirectory;
    var packageName = sourcePackageName + subpackage;
    try {
      var path = Path.of(outputDirectory);

      Files.createDirectories(path);

      JavaFile.builder(packageName, typeSpec).build().writeTo(path);
    } catch (IOException e) {
      throw new IllegalStateException("cannot write sources to: " + outputDirectory, e);
    }
  }
}

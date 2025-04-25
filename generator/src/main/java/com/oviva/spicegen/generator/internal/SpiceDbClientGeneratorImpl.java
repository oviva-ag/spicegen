package com.oviva.spicegen.generator.internal;

import static com.oviva.spicegen.generator.utils.TextUtils.toPascalCase;

import com.oviva.spicegen.api.CheckPermission;
import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.LookupSuspects;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.ObjectRefFactory;
import com.oviva.spicegen.api.SubjectRef;
import com.oviva.spicegen.api.UpdateRelationship;
import com.oviva.spicegen.generator.Options;
import com.oviva.spicegen.generator.SpiceDbClientGenerator;
import com.oviva.spicegen.generator.utils.TextUtils;
import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.ObjectTypeRef;
import com.oviva.spicegen.model.Permission;
import com.oviva.spicegen.model.Relation;
import com.oviva.spicegen.model.Schema;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
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
  private final TypeName objectRefTypeName = ClassName.get(ObjectRef.class);
  private final TypeName updateRelationshipTypeName = ClassName.get(UpdateRelationship.class);

  private final Options options;

  public SpiceDbClientGeneratorImpl(Options options) {
    this.options = options;
  }

  private TypeSpecStore typeSpecStore;

  @Override
  public void generate(Schema spec) {
    typeSpecStore = new TypeSpecStore();
    generateConstants(spec);
    generateRefs(spec);
  }

  private void generateConstants(Schema spec) {

    TypeSpec.Builder constants =
        TypeSpec.classBuilder("SchemaConstants").addModifiers(Modifier.PUBLIC);

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

    TypeSpec.Builder factories =
        TypeSpec.classBuilder("ObjectRefFactories")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            // private ctor to prevent instantiation
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("throw new AssertionError(\"No instances allowed\")")
                    .build());

    for (ObjectDefinition definition : spec.definitions()) {
      var className = TextUtils.capitalize(TextUtils.toCamelCase(definition.name())) + "Ref";
      var typedRef = TypeSpec.classBuilder(className).build();
      if (typeSpecStore.has(typedRef.name)) {
        return;
      }
      typeSpecStore.put(typedRef);

      var typedRefBuilder =
          typedRef.toBuilder()
              .addSuperinterface(objectRefTypeName)
              .addModifiers(Modifier.PUBLIC)
              .addField(
                  FieldSpec.builder(
                          String.class, "kind", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                      .initializer("$S", definition.name())
                      .build())
              .addField(String.class, "id", Modifier.PRIVATE, Modifier.FINAL)
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

      addCheckMethods(typedRefBuilder, definition);
      addBulkCheckMethods(typedRefBuilder, definition);
      addLookupMethods(typedRefBuilder, definition);

      typedRef = typedRefBuilder.build();
      writeSource(typedRef, ".refs");
      createFactoryInstance(ClassName.bestGuess(className), factories);
    }

    TypeSpec factoriesClass = factories.build();

    writeSource(factoriesClass, ".refs");
  }

  private static void createFactoryInstance(ClassName ref, Builder factories) {
    ParameterizedTypeName factoryInterface =
        ParameterizedTypeName.get(ClassName.get(ObjectRefFactory.class), ref);

    TypeSpec impl =
        TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(factoryInterface)
            .addField(
                FieldSpec.builder(
                        String.class, "kind", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.of(\"\").kind()", ref)
                    .build())

            // getRefClass()
            .addMethod(
                MethodSpec.methodBuilder("getRefClass")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(Class.class), ref))
                    .addStatement("return $T.class", ref)
                    .build())

            // create(String id)
            .addMethod(
                MethodSpec.methodBuilder("create")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "id")
                    .returns(ref)
                    .addStatement("return $T.of(id)", ref)
                    .build())

            // kind()
            .addMethod(
                MethodSpec.methodBuilder("kind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
                    .addStatement("return kind")
                    .build())
            .build();

    factories.addField(
        FieldSpec.builder(
                factoryInterface,
                ref.simpleName().toUpperCase(),
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL)
            .initializer("$L", impl)
            .build());
  }

  private void addCheckMethods(TypeSpec.Builder typeRefBuilder, ObjectDefinition definition) {
    for (Permission permission : definition.permissions()) {

      var permissionName = TextUtils.toPascalCase(permission.name());
      var checkMethod = "check" + permissionName;

      var subjectParamName = "subject";
      var consistencyParamName = "consistency";

      typeRefBuilder.addMethod(
          MethodSpec.methodBuilder(checkMethod)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(ClassName.get(SubjectRef.class), subjectParamName)
              .addParameter(ClassName.get(Consistency.class), consistencyParamName)
              .returns(ClassName.get(CheckPermission.class))
              .addCode(
                  """
                if ($L == null) {
                 throw new IllegalArgumentException("subject must not be null");
                }
                return CheckPermission.newBuilder().resource(this).permission($S).subject($L).consistency($L).build();
              """,
                  subjectParamName,
                  permission.name(),
                  subjectParamName,
                  consistencyParamName)
              .build());
    }
  }

  private void addBulkCheckMethods(TypeSpec.Builder typeRefBuilder, ObjectDefinition definition) {
    for (Permission permission : definition.permissions()) {

      var permissionName = TextUtils.toPascalCase(permission.name());
      var checkMethod = "checkBulk" + permissionName;

      var subjectParamName = "subject";

      typeRefBuilder.addMethod(
          MethodSpec.methodBuilder(checkMethod)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(ClassName.get(SubjectRef.class), subjectParamName)
              .returns(ClassName.get(CheckBulkPermissionItem.class))
              .addCode(
                  """
                                if ($L == null) {
                                 throw new IllegalArgumentException("subject must not be null");
                                }
                                return CheckBulkPermissionItem.newBuilder().resource(this).permission($S).subject($L).build();
                              """,
                  subjectParamName,
                  permission.name(),
                  subjectParamName)
              .build());
    }
  }

  private void addLookupMethods(TypeSpec.Builder typeRefBuilder, ObjectDefinition definition) {
    for (Permission permission : definition.permissions()) {

      var allowedObjectTypes =
          definition.relations().stream()
              .flatMap(relation -> relation.allowedObjects().stream())
              .toList();

      var permissionName = TextUtils.toPascalCase(permission.name());

      for (var allowedObjectType : allowedObjectTypes) {

        // TODO magic ref
        String refType = toPascalCase(allowedObjectType.typeName());
        var typeRefName = refType + "Ref";

        ClassName className = ClassName.bestGuess(typeRefName);
        var relationshipName = Objects.requireNonNullElse(allowedObjectType.relationship(), "");
        var lookupMethodName =
            "lookup%s%s%s".formatted(permissionName, refType, toPascalCase(relationshipName));

        typeRefBuilder.addMethod(
            MethodSpec.methodBuilder(lookupMethodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(LookupSuspects.class), className))
                .addCode(
                    """
                  return LookupSuspects.<$T>newBuilder().resource(this).permission($S).subjectType(ObjectRefFactories.$L).subjectRelation($S).build();
                """,
                    className,
                    permission.name(),
                    className.simpleName().toUpperCase(),
                    allowedObjectType.relationship())
                .build());
      }
    }
  }

  private void addUpdateMethods(TypeSpec.Builder typeRefBuilder, ObjectDefinition definition) {

    for (Relation relation : definition.relations()) {

      var relationCamelCase = TextUtils.toPascalCase(relation.name());

      for (ObjectTypeRef allowedObject : relation.allowedObjects()) {
        var relationshipName =
            allowedObject.relationship() == null ? "" : toPascalCase(allowedObject.relationship());
        // add create
        var createMethod =
            "create%s%s%s"
                .formatted(
                    relationCamelCase,
                    TextUtils.toPascalCase(allowedObject.typeName()),
                    relationshipName);

        // TODO magic ref
        var typeRefName = toPascalCase(allowedObject.typeName()) + "Ref";

        typeRefBuilder.addMethod(
            MethodSpec.methodBuilder(createMethod)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(options.packageName() + ".refs", typeRefName), "ref")
                .returns(updateRelationshipTypeName)
                .addCode(
                    """
                    if ($L == null) {
                     throw new IllegalArgumentException("ref must not be null");
                    }
                    return $T.ofUpdate(this, $S, $T.ofObjectWithRelation($L, $S));
                    """,
                    "ref",
                    updateRelationshipTypeName,
                    relation.name(),
                    SubjectRef.class,
                    "ref",
                    allowedObject.relationship())
                .build());

        var deleteMethod =
            "delete%s%s%s"
                .formatted(
                    relationCamelCase,
                    TextUtils.toPascalCase(allowedObject.typeName()),
                    relationshipName);

        typeRefBuilder.addMethod(
            MethodSpec.methodBuilder(deleteMethod)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(options.packageName() + ".refs", typeRefName), "ref")
                .returns(ClassName.bestGuess("UpdateRelationship"))
                .addCode(
                    """
                    if ($L == null) {
                     throw new IllegalArgumentException("ref must not be null");
                    }
                    return $T.ofDelete(this, $S, SubjectRef.ofObjectWithRelation($L, $S));
                    """,
                    "ref",
                    updateRelationshipTypeName,
                    relation.name(),
                    "ref",
                    allowedObject.relationship())
                .build());
      }
    }
  }

  private void writeSource(TypeSpec typeSpec, String subpackage) {
    var outputDirectory = options.outputDirectory();
    var packageName = options.packageName() + subpackage;
    try {
      var path = Path.of(outputDirectory);

      Files.createDirectories(path);

      JavaFile.builder(packageName, typeSpec).build().writeTo(path);
    } catch (IOException e) {
      throw new IllegalStateException("cannot write sources to: " + outputDirectory, e);
    }
  }
}

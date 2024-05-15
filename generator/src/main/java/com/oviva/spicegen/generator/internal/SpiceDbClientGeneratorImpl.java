package com.oviva.spicegen.generator.internal;

import com.oviva.spicegen.generator.SpiceDbClientGenerator;
import com.oviva.spicegen.generator.utils.TextUtils;
import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.Permission;
import com.oviva.spicegen.model.Relation;
import com.oviva.spicegen.model.Schema;
import com.squareup.javapoet.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.lang.model.element.Modifier;

public class SpiceDbClientGeneratorImpl implements SpiceDbClientGenerator {
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
    var objectRef = TypeSpec.interfaceBuilder("ObjectRef").build();
    if (typeSpecStore.has(objectRef.name)) {
      return;
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

    for (ObjectDefinition definition : spec.definitions()) {
      var className = TextUtils.capitalize(TextUtils.toCamelCase(definition.name())) + "Ref";
      var typedRef = TypeSpec.classBuilder(className).build();
      if (typeSpecStore.has(typedRef.name)) {
        return;
      }
      typeSpecStore.put(typedRef);

      typedRef =
          typedRef.toBuilder()
              .addSuperinterface(ClassName.bestGuess("ObjectRef"))
              .addModifiers(Modifier.PUBLIC)
              .addField(
                  FieldSpec.builder(String.class, "kind", Modifier.PRIVATE, Modifier.STATIC)
                      .initializer("\"" + definition.name() + "\"")
                      .build())
              .addField(String.class, "id", Modifier.PRIVATE)
              .addMethod(
                  MethodSpec.constructorBuilder()
                      .addParameter(String.class, "kind")
                      .addParameter(String.class, "id")
                      .addStatement("this.kind = kind")
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
                              + "return new $T(kind, id.toString());",
                          ClassName.bestGuess(className))
                      .build())
              .addMethod(
                  MethodSpec.methodBuilder("ofLong")
                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                      .returns(ClassName.bestGuess(className))
                      .addParameter(TypeName.LONG, "id")
                      .addCode(
                          "return new $T(kind, String.valueOf(id));",
                          ClassName.bestGuess(className))
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
                              + "return new $T(kind, id);",
                          ClassName.bestGuess(className))
                      .build())
              .build();
      writeSource(typedRef, ".refs");
    }
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

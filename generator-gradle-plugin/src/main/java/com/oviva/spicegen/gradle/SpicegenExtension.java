package com.oviva.spicegen.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring the spicegen code generator.
 *
 * <p>Usage in build.gradle.kts:
 *
 * <pre>
 * spicegen {
 *     schemaFile.set(file("src/main/resources/schema.zed"))
 *     outputDirectory.set(layout.buildDirectory.dir("generated/sources/spicegen/java/main"))
 *     packageName.set("com.example.permissions")
 * }
 * </pre>
 */
public abstract class SpicegenExtension {

  /** Path to the SpiceDB schema file (.zed file). Default: src/main/resources/schema.zed */
  public abstract RegularFileProperty getSchemaFile();

  /**
   * Output directory for generated Java sources. Default:
   * ${buildDir}/generated/sources/spicegen/java/main
   */
  public abstract DirectoryProperty getOutputDirectory();

  /** Package name for generated classes. Default: ${project.group}.permissions */
  public abstract Property<String> getPackageName();
}

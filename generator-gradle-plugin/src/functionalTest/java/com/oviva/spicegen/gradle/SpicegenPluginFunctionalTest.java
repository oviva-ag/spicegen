package com.oviva.spicegen.gradle;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SpicegenPluginFunctionalTest {

  @TempDir File projectDir;

  private File buildFile;
  private File schemaFile;

  @BeforeEach
  void setup() throws IOException {
    buildFile = new File(projectDir, "build.gradle.kts");

    // Create schema directory
    var resourcesDir = new File(projectDir, "src/main/resources");
    resourcesDir.mkdirs();
    schemaFile = new File(resourcesDir, "schema.zed");
  }

  @Test
  void canGenerateSourcesFromSchema() throws IOException {
    // Write build file
    Files.writeString(
        buildFile.toPath(),
        """
            plugins {
                java
                id("com.oviva.spicegen")
            }

            group = "com.example"
            version = "1.0.0"

            repositories {
                mavenCentral()
                mavenLocal()
            }

            dependencies {
                implementation("com.oviva.spicegen:api:1.0.0-SNAPSHOT")
            }

            spicegen {
                packageName.set("com.example.permissions")
            }
            """);

    // Write schema file
    Files.writeString(
        schemaFile.toPath(),
        """
            definition user {}

            definition document {
                relation reader: user
                permission read = reader
            }
            """);

    // Run the build
    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("generateSpiceDbSources", "--stacktrace")
            .forwardOutput()
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":generateSpiceDbSources").getOutcome());

    // Verify generated files
    var generatedDir = new File(projectDir, "build/generated/sources/spicegen/java/main");
    assertTrue(generatedDir.exists(), "Generated directory should exist");

    var permissionsPackageDir = new File(generatedDir, "com/example/permissions");
    assertTrue(permissionsPackageDir.exists(), "Package directory should exist");
  }

  @Test
  void taskIsUpToDateOnRerun() throws IOException {
    // Setup project files
    Files.writeString(
        buildFile.toPath(),
        """
            plugins {
                java
                id("com.oviva.spicegen")
            }

            group = "com.example"

            repositories {
                mavenCentral()
                mavenLocal()
            }

            dependencies {
                implementation("com.oviva.spicegen:api:1.0.0-SNAPSHOT")
            }
            """);

    Files.writeString(
        schemaFile.toPath(),
        """
            definition user {}
            """);

    // First run
    var result1 =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("generateSpiceDbSources")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result1.task(":generateSpiceDbSources").getOutcome());

    // Second run (should be UP-TO-DATE)
    var result2 =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("generateSpiceDbSources")
            .build();

    assertEquals(TaskOutcome.UP_TO_DATE, result2.task(":generateSpiceDbSources").getOutcome());
  }

  @Test
  void supportsCustomConfiguration() throws IOException {
    // Create custom schema location
    var customSchemaDir = new File(projectDir, "schemas");
    customSchemaDir.mkdirs();
    var customSchema = new File(customSchemaDir, "permissions.zed");
    Files.writeString(
        customSchema.toPath(),
        """
            definition user {}
            """);

    Files.writeString(
        buildFile.toPath(),
        """
            plugins {
                java
                id("com.oviva.spicegen")
            }

            repositories {
                mavenCentral()
                mavenLocal()
            }

            dependencies {
                implementation("com.oviva.spicegen:api:1.0.0-SNAPSHOT")
            }

            spicegen {
                schemaFile.set(file("schemas/permissions.zed"))
                outputDirectory.set(layout.buildDirectory.dir("gen/java"))
                packageName.set("my.custom.pkg")
            }
            """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("generateSpiceDbSources")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":generateSpiceDbSources").getOutcome());

    var customOutput = new File(projectDir, "build/gen/java");
    assertTrue(customOutput.exists(), "Custom output directory should exist");

    var customPackageDir = new File(customOutput, "my/custom/pkg");
    assertTrue(customPackageDir.exists(), "Custom package directory should exist");
  }

  @Test
  void compileJavaDependsOnGenerateTask() throws IOException {
    Files.writeString(
        buildFile.toPath(),
        """
            plugins {
                java
                id("com.oviva.spicegen")
            }

            group = "com.example"

            repositories {
                mavenCentral()
                mavenLocal()
            }

            dependencies {
                implementation("com.oviva.spicegen:api:1.0.0-SNAPSHOT")
            }
            """);

    Files.writeString(
        schemaFile.toPath(),
        """
            definition user {}

            definition document {
                relation reader: user
                permission read = reader
            }
            """);

    // Running compileJava should trigger generateSpiceDbSources
    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("compileJava", "--stacktrace")
            .forwardOutput()
            .build();

    // Both tasks should have run
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateSpiceDbSources").getOutcome());
    assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
  }
}

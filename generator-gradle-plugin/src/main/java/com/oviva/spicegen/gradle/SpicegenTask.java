package com.oviva.spicegen.gradle;

import com.oviva.spicegen.generator.Options;
import com.oviva.spicegen.generator.internal.SpiceDbClientGeneratorImpl;
import com.oviva.spicegen.parser.SpiceDbSchemaParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

/** Gradle task that generates type-safe SpiceDB client code from a .zed schema file. */
@CacheableTask
public abstract class SpicegenTask extends DefaultTask {

  @InputFile
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract RegularFileProperty getSchemaFile();

  @OutputDirectory
  public abstract DirectoryProperty getOutputDirectory();

  @Input
  public abstract Property<String> getPackageName();

  @TaskAction
  public void generate() {
    var schemaPath = getSchemaFile().get().getAsFile().toPath();
    var outputDir = getOutputDirectory().get().getAsFile().getAbsolutePath();
    var packageName = getPackageName().get();

    getLogger().lifecycle("Reading SpiceDB schema from '{}'", schemaPath);

    var parser = new SpiceDbSchemaParser();
    var spec = parser.parse(schemaPath);

    getLogger()
        .lifecycle("Generating SpiceDB sources to '{}' with package '{}'", outputDir, packageName);

    var options = new Options(outputDir, packageName);
    var generator = new SpiceDbClientGeneratorImpl(options);
    generator.generate(spec);

    getLogger().lifecycle("SpiceDB client generation complete");
  }
}

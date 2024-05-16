package com.oviva.spicegen.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.oviva.spicegen.generator.Options;
import com.oviva.spicegen.generator.internal.SpiceDbClientGeneratorImpl;
import com.oviva.spicegen.parser.SpiceDbSchemaParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "spicegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SpicegenMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter(
      defaultValue = "${project.basedir}/src/main/resources/schema_ast.json",
      //          defaultValue = "${project.basedir}/src/main/resources/schema.zed",
      property = "schemaPath")
  String schemaPath;

  @Parameter(
      defaultValue = "${project.basedir}/target/generated-sources/src/main/java",
      property = "outputDirectory")
  String outputDirectory;

  @Parameter(defaultValue = "${project.groupId}.permissions", property = "packageName")
  String packageName;

  public void execute() throws MojoExecutionException {
    var log = getLog();

    log.info("reading schema from '" + schemaPath + "'");
    var specInputStream = readSchema();

    var generator = new SpiceDbClientGeneratorImpl(new Options(outputDirectory, packageName));
    var parser = new SpiceDbSchemaParser();

    log.info("parsing schema");

    // TODO parse direct zed schema
    var spec = parser.parse(specInputStream);

    log.info("generating SpiceDB sources and writing to '" + outputDirectory + "'");
    generator.generate(spec);

    project.addCompileSourceRoot(outputDirectory);
  }

  private InputStream readSchema() throws MojoExecutionException {

    try {
      return Files.newInputStream(Path.of(schemaPath), StandardOpenOption.READ);
    } catch (IOException e) {
      throw new MojoExecutionException(
          "cannot find asyncapi specification at: '" + schemaPath + "'", e);
    }
  }
}

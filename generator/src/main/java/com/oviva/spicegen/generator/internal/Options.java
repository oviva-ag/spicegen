package com.oviva.spicegen.generator.internal;

public class Options {
  private final String outputDirectory;
  private final String packageName;

  private final boolean generateBuilders;

  public Options(String outputDirectory, String packageName, boolean generateBuilders) {
    this.outputDirectory = outputDirectory;
    this.packageName = packageName;
    this.generateBuilders = generateBuilders;
  }

  public String outputDirectory() {
    return outputDirectory;
  }

  public String packageName() {
    return packageName;
  }

  public boolean generateBuilders() {
    return generateBuilders;
  }
}

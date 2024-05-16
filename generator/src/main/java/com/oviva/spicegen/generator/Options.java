package com.oviva.spicegen.generator;

public class Options {
  private final String outputDirectory;
  private final String packageName;

  public Options(String outputDirectory, String packageName) {
    this.outputDirectory = outputDirectory;
    this.packageName = packageName;
  }

  public String outputDirectory() {
    return outputDirectory;
  }

  public String packageName() {
    return packageName;
  }
}

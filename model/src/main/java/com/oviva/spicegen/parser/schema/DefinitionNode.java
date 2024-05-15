package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefinitionNode extends BaseNode {
  @JsonProperty("name")
  private String name;

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return "DefinitionNode{"
        + "name='"
        + name
        + '\''
        + ", nodeType="
        + nodeType
        + ", children="
        + children
        + '}';
  }
}

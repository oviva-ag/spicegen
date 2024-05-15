package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DefinitionNode extends AbstractNode implements Node {
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

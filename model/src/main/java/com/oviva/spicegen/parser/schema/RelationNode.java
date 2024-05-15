package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RelationNode extends BaseNode {
  @JsonProperty("name")
  private String name;

  @JsonProperty("allowed_types")
  private List<BaseNode> allowedTypes;

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return "RelationNode{" +
            "name='" + name + '\'' +
            ", allowedTypes=" + allowedTypes +
            ", nodeType=" + nodeType +
            ", children=" + children +
            '}';
  }
}

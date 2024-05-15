package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class RelationNode extends AbstractNode implements Node {
  @JsonProperty("name")
  private String name;

  @JsonProperty("allowed_types")
  private List<TypeRefNode> allowedTypes;

  public String name() {
    return name;
  }

  public List<TypeRefNode> allowedTypes() {
    return allowedTypes;
  }

  @Override
  public String toString() {
    return "RelationNode{"
        + "name='"
        + name
        + '\''
        + ", allowedTypes="
        + allowedTypes
        + ", nodeType="
        + nodeType
        + ", children="
        + children
        + '}';
  }
}

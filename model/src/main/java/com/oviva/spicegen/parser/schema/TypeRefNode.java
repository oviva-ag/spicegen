package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class TypeRefNode extends AbstractNode implements Node {
  @JsonProperty("type_ref_types")
  private List<Node> typeRefTypes;

  public List<Node> typeRefTypes() {
    return typeRefTypes;
  }

  @Override
  public String toString() {
    return "TypeRefNode{"
        + "typeRefTypes="
        + typeRefTypes
        + ", nodeType="
        + nodeType
        + ", children="
        + children
        + '}';
  }
}

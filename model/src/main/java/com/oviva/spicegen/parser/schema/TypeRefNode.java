package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TypeRefNode extends BaseNode {
  @JsonProperty("type_ref_types")
  private List<BaseNode> typeRefTypes;

  public List<BaseNode> typeRefTypes() {
    return typeRefTypes;
  }

  @Override
  public String toString() {
    return "TypeRefNode{" +
            "typeRefTypes=" + typeRefTypes +
            ", nodeType=" + nodeType +
            ", children=" + children +
            '}';
  }
}

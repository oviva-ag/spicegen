package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SpecificTypeRefNode extends BaseNode {

  @JsonProperty("type_name")
  private String typeName;

  @JsonProperty("relation_name")
  private String relationName;

  public String typeName() {
    return typeName;
  }

  public String relationName() {
    return relationName;
  }

  @Override
  public String toString() {
    return "SpecificTypeRefNode{" +
            "typeName='" + typeName + '\'' +
            ", relationName='" + relationName + '\'' +
            ", nodeType=" + nodeType +
            ", children=" + children +
            '}';
  }
}

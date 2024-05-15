package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class AbstractNode {

  @JsonProperty("type")
  protected NodeType nodeType;

  @JsonProperty("children")
  protected List<Node> children;

  public NodeType kind() {
    return nodeType;
  }

  public List<Node> children() {
    return children;
  }
}

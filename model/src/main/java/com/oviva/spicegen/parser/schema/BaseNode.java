package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BaseNode implements Node {

  @JsonProperty("type")
  protected NodeType nodeType;

  @JsonProperty("children")
  protected List<Node> children;

  @Override
  public NodeType kind() {
    return nodeType;
  }

  public List<Node> children() {
    return children;
  }

  @Override
  public String toString() {
    return "BaseNode{" + "nodeType=" + nodeType + ", children=" + children + '}';
  }
}

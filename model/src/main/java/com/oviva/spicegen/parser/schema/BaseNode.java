package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class BaseNode extends AbstractNode implements Node {

  @Override
  public String toString() {
    return "BaseNode{" + "nodeType=" + nodeType + ", children=" + children + '}';
  }
}

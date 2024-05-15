package com.oviva.spicegen.parser.schema;

public final class BaseNode extends AbstractNode implements Node {

  @Override
  public String toString() {
    return "BaseNode{" + "nodeType=" + nodeType + ", children=" + children + '}';
  }
}

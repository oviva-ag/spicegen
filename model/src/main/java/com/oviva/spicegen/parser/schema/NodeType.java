package com.oviva.spicegen.parser.schema;

public enum NodeType {
  // see: parser/model.go:10
  NodeTypeError,
  NodeTypeFile,
  NodeTypeComment,
  NodeTypeDefinition,
  NodeTypeRelation,
  NodeTypePermission,
  NodeTypeTypeReference,
  NodeTypeSpecificTypeReference,
  NodeTypeIdentifier
}

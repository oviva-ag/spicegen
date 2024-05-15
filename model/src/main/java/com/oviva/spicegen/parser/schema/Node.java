package com.oviva.spicegen.parser.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BaseNode.class, name = "NodeTypeFile"),
  @JsonSubTypes.Type(value = DefinitionNode.class, name = "NodeTypeDefinition"),
  @JsonSubTypes.Type(value = RelationNode.class, name = "NodeTypeRelation"),
  @JsonSubTypes.Type(value = BaseNode.class, name = "NodeTypeComment"),
  @JsonSubTypes.Type(value = TypeRefNode.class, name = "NodeTypeTypeReference"),
  @JsonSubTypes.Type(value = PermissionNode.class, name = "NodeTypePermission"),
  @JsonSubTypes.Type(value = SpecificTypeRefNode.class, name = "NodeTypeSpecificTypeReference")
})
public sealed interface Node
    permits BaseNode,
        DefinitionNode,
        PermissionNode,
        RelationNode,
        SpecificTypeRefNode,
        TypeRefNode {
  NodeType kind();

  default <T extends Node> T unwrap(Class<T> clazz) {
    return (T) this;
  }
}

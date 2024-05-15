package com.oviva.spicegen.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviva.spicegen.model.*;
import com.oviva.spicegen.parser.schema.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SpiceDbSchemaParser {

  public Schema parse(InputStream is) {

    try {
      var root = tryParseAst(is);
      if (root.kind() != NodeType.NodeTypeFile) {
        throw new IllegalArgumentException(
            "unexpected root node: %s".formatted(root.kind().name()));
      }

      var definitions =
          streamNullable(root.unwrap(BaseNode.class).children())
              .filter(byKind(NodeType.NodeTypeDefinition))
              .map(d -> d.unwrap(DefinitionNode.class))
              .map(this::mapDefinition)
              .toList();

      return new Schema(definitions);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ObjectDefinition mapDefinition(DefinitionNode n) {
    var relations =
        streamNullable(n.children())
            .filter(byKind(NodeType.NodeTypeRelation))
            .map(node -> node.unwrap(RelationNode.class))
            .map(this::mapRelation)
            .toList();

    var permissions =
        streamNullable(n.children())
            .filter(byKind(NodeType.NodeTypePermission))
            .map(node -> node.unwrap(PermissionNode.class))
            .map(this::mapPermission)
            .toList();

    return new ObjectDefinition(n.name(), relations, permissions);
  }

  private Permission mapPermission(PermissionNode n) {
    return new Permission(n.name());
  }

  private Relation mapRelation(RelationNode n) {

    var allowedTypes =
        streamNullable(n.allowedTypes())
            .filter(byKind(NodeType.NodeTypeTypeReference))
            .map(node -> node.unwrap(TypeRefNode.class))
            .flatMap(t -> t.typeRefTypes().stream())
            .filter(byKind(NodeType.NodeTypeSpecificTypeReference))
            .map(node -> (SpecificTypeRefNode) node)
            .map(node -> new ObjectTypeRef(node.typeName(), node.relationName()))
            .toList();

    return new Relation(n.name(), allowedTypes);
  }

  private static Predicate<Node> byKind(NodeType kind) {
    return n -> n.kind() == kind;
  }

  private static <T> Stream<T> streamNullable(Collection<T> t) {
    return Optional.ofNullable(t).stream().flatMap(Collection::stream);
  }

  private Node tryParseAst(InputStream is) throws IOException {

    var om = new ObjectMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    var root = om.readValue(is, Node.class);
    return root;
  }
}

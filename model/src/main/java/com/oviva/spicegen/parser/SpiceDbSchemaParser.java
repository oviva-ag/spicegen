package com.oviva.spicegen.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviva.spicegen.model.*;
import com.oviva.spicegen.parser.schema.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiceDbSchemaParser {

  private static Logger logger = LoggerFactory.getLogger(SpiceDbSchemaParser.class);

  public Schema parse(Path schema) {

    Path astPath = null;
    try {

      var preprocessor = new AstPreProcessor();

      var name = schema.getFileName().toString();
      name = name.substring(0, name.lastIndexOf('.'));

      astPath = Files.createTempFile("%s_ast_".formatted(name), ".json");

      logger.info("pre-processing schema into AST from {} to {}", schema, astPath);
      preprocessor.parse(astPath, schema);

      logger.info("loading AST from {}", astPath);
      var root = loadAst(astPath);

      logger.debug("parsing schema from AST");
      var definitions =
          streamNullable(root.unwrap(BaseNode.class).children())
              .filter(byKind(NodeType.NodeTypeDefinition))
              .map(d -> d.unwrap(DefinitionNode.class))
              .map(this::mapDefinition)
              .toList();

      logger.debug("schema parsed");

      return new Schema(definitions);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      deleteQuietly(astPath);
    }
  }

  private Node loadAst(Path astPath) {

    try (var is = Files.newInputStream(astPath)) {
      var root = tryParseAst(is);
      if (root.kind() != NodeType.NodeTypeFile) {
        throw new IllegalArgumentException(
            "unexpected root node: %s".formatted(root.kind().name()));
      }
      return root;
    } catch (IOException e) {
      throw new IllegalStateException("failed to load AST from '%s'".formatted(astPath), e);
    }
  }

  private void deleteQuietly(Path p) {
    if (p == null) {
      return;
    }

    try {
      Files.deleteIfExists(p);
    } catch (IOException e) {
      // ignore quietly
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

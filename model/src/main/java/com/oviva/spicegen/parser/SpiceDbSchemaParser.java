package com.oviva.spicegen.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.Schema;
import com.oviva.spicegen.parser.schema.BaseNode;
import com.oviva.spicegen.parser.schema.DefinitionNode;
import com.oviva.spicegen.parser.schema.Node;
import com.oviva.spicegen.parser.schema.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SpiceDbSchemaParser {

  public Schema parse(InputStream is) {

    try {
      var root = tryParseAst(is);
      if (root.kind() != NodeType.NodeTypeFile) {
        throw new IllegalArgumentException(
            "unexpected root node: %s".formatted(root.kind().name()));
      }

      var definitions =
          ((BaseNode) root)
              .children().stream()
                  .filter(n -> n.kind() == NodeType.NodeTypeDefinition)
                  .map(d -> (DefinitionNode) d)
                  .map(o -> new ObjectDefinition(o.name(), List.of(), List.of()))
                  .toList();

      return new Schema(definitions);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Node tryParseAst(InputStream is) throws IOException {

    var om = new ObjectMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    var root = om.readValue(is, Node.class);
    return root;
  }
}

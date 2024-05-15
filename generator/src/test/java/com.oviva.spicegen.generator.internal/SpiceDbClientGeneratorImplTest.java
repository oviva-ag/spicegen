package com.oviva.spicegen.generator.internal;

import com.oviva.spicegen.model.ObjectDefinition;
import com.oviva.spicegen.model.ObjectTypeRef;
import com.oviva.spicegen.model.Relation;
import com.oviva.spicegen.model.Schema;
import com.oviva.spicegen.parser.SpiceDbSchemaParser;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SpiceDbClientGeneratorImplTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        //                  "files",
        "oviva",
      })
  void test() {

    var generator = new SpiceDbClientGeneratorImpl();
    var objectDefinition2 = new ObjectDefinition("foo", List.of(), List.of());
    var objectDefinition1 =
        new ObjectDefinition(
            "hi",
            List.of(new Relation("son_of", List.of(new ObjectTypeRef("foo", null)))),
            List.of());
    var definitions = List.of(objectDefinition1, objectDefinition2);
    var schema = new Schema(definitions);
    schema = loadSchema("oviva");
    generator.generate(schema);
  }

  private Schema loadSchema(String name) {

    var astInputStream =
        this.getClass().getResourceAsStream("/fixtures/%s_ast.json".formatted(name));
    var parser = new SpiceDbSchemaParser();

    return parser.parse(astInputStream);
  }
}

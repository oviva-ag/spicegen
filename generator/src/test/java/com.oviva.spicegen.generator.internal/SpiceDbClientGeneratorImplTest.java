package com.oviva.spicegen.generator.internal;

import com.oviva.spicegen.model.Schema;
import com.oviva.spicegen.parser.SpiceDbSchemaParser;
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
    var schema = loadSchema("oviva");
    generator.generate(schema);
  }

  private Schema loadSchema(String name) {

    var astInputStream =
        this.getClass().getResourceAsStream("/fixtures/%s_ast.json".formatted(name));
    var parser = new SpiceDbSchemaParser();

    return parser.parse(astInputStream);
  }
}

package com.oviva.spicegen.parser;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SpiceDbSchemaParserTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "files", "oviva",
      })
  void test_parsingVersion2AndVersion3ProducesSameResults(String file) {
    // Given
    var astInputStream = this.getClass().getResourceAsStream("/%s_ast.json".formatted(file));
    var parser = new SpiceDbSchemaParser();

    var schema = parser.parse(astInputStream);

    System.out.println(schema);
  }
}

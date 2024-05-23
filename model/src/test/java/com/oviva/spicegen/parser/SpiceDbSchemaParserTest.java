package com.oviva.spicegen.parser;

import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SpiceDbSchemaParserTest {

  @ParameterizedTest
  @ValueSource(strings = {"basic", "files", "groups", "platform", "recursive", "synthetic"})
  void test_parsingVersion2AndVersion3ProducesSameResults(String file) {

    var astInput = Path.of("./src/test/resources/%s.zed".formatted(file));
    var parser = new SpiceDbSchemaParser();

    parser.parse(astInput);
  }
}

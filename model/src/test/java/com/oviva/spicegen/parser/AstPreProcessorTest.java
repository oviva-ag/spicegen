package com.oviva.spicegen.parser;

import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AstPreProcessorTest {

  @TempDir Path temp;

  @ParameterizedTest
  @ValueSource(strings = {"basic", "files", "groups", "platform", "recursive", "synthetic"})
  void test_process(String schemaName) {
    var preprocessor = new AstPreProcessor();

    var schema = Path.of("./src/test/resources/%s.zed".formatted(schemaName));
    var out = Path.of(temp.toString(), "%s_ast.json".formatted(schemaName));
    preprocessor.parse(out, schema);
  }
}

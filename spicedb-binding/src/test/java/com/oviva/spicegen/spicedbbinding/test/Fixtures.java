package com.oviva.spicegen.spicedbbinding.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Fixtures {

  public static String contractTestSchema() {
    var data = readFixture("/spicedb_contract_test_schema.zed");
    return new String(data, StandardCharsets.UTF_8);
  }

  private static byte[] readFixture(String path) {

    try (var is = Fixtures.class.getResourceAsStream(path)) {
      if (is == null) {
        throw new IllegalArgumentException("resource not found: " + path);
      }
      return is.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("cannot read resource: " + path, e);
    }
  }
}

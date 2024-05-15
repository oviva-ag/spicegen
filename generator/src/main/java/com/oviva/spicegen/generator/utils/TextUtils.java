package com.oviva.spicegen.generator.utils;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {

  private TextUtils() {}

  public static String toCamelCase(String snakeCaseText) {
    return Pattern.compile("_([a-z])")
        .matcher(snakeCaseText)
        .replaceAll(m -> m.group(1).toUpperCase());
  }

  public static String toPascalCase(String text) {
    return Pattern.compile("[^A-Za-z0-9]")
        .splitAsStream(text)
        .map(TextUtils::capitalize)
        .collect(Collectors.joining());
  }

  public static String capitalize(String s) {
    if (s == null || s.length() < 1) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  public static String lastOf(String text, String separator) {
    var split = text.split(separator);
    return Arrays.stream(split)
        .skip(split.length - 1L)
        .findFirst()
        .filter(s -> !s.isBlank())
        .orElse(null);
  }
}

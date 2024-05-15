package com.oviva.spicegen.generator;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ObjectRef {

  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[-_a-z0-9]+");

  private static String kind;
  private final String id;

  public static ObjectRef ofUuid(String namespace, UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }

    return of(id.toString());
  }

  public static ObjectRef ofLong(long id) {
    return of(String.valueOf(id));
  }

  public static ObjectRef of(String id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }

    return new ObjectRef(kind, id);
  }

  private static boolean isValidNamespace(String ns) {
    if (ns == null) {
      return false;
    }

    return NAMESPACE_PATTERN.matcher(ns).matches();
  }

  private ObjectRef(String kind, String id) {
    this.kind = kind;
    this.id = id;
  }

  public String kind() {
    return kind;
  }

  public String id() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectRef objectRef = (ObjectRef) o;
    return Objects.equals(kind, objectRef.kind) && Objects.equals(id, objectRef.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, id);
  }

  @Override
  public String toString() {
    return kind + ":" + id;
  }
}

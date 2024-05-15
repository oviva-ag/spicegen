package com.oviva.spicegen.generator;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class SubjectRef {

  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[_a-z]+");
  private static final String NAMESPACE_USER = "user";

  private final String kind;
  private final String id;

  public static SubjectRef ofUser(UUID id) {
    return ofUuid(NAMESPACE_USER, id);
  }

  public static SubjectRef ofUuid(String namespace, UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }

    return of(namespace, id.toString());
  }

  public static SubjectRef of(String namespace, String id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (!isValidNamespace(namespace)) {
      throw new IllegalArgumentException("invalid namespace: " + namespace);
    }
    return new SubjectRef(namespace, id);
  }

  private static boolean isValidNamespace(String ns) {
    if (ns == null) {
      return false;
    }

    return NAMESPACE_PATTERN.matcher(ns).matches();
  }

  private SubjectRef(String kind, String id) {
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
    SubjectRef that = (SubjectRef) o;
    return Objects.equals(kind, that.kind) && Objects.equals(id, that.id);
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

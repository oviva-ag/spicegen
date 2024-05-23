package com.oviva.spicegen.api;

import java.util.Objects;

public interface ObjectRef {
  String kind();

  String id();

  static ObjectRef of(String kind, String id) {
    if (kind == null) {
      throw new IllegalArgumentException("kind must  not be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("id must  not be null");
    }
    return new ObjectRef() {
      @Override
      public String kind() {
        return kind;
      }

      @Override
      public String id() {
        return id;
      }

      @Override
      public String toString() {
        return "%s:%s".formatted(kind, id);
      }

      @Override
      public int hashCode() {
        return Objects.hash(kind, id);
      }

      @Override
      public boolean equals(Object obj) {
        if (!(obj instanceof ObjectRef ref)) {
          return false;
        }

        return Objects.equals(this.kind(), ref.kind()) && Objects.equals(this.id(), ref.id());
      }
    };
  }
}

package com.oviva.spicegen.api;

import java.util.Objects;

public interface SubjectRef {
  String kind();

  String id();

  static SubjectRef ofObject(ObjectRef o) {
    return new SubjectRef() {
      @Override
      public String kind() {
        return o.kind();
      }

      @Override
      public String id() {
        return o.id();
      }

      @Override
      public int hashCode() {
        return Objects.hash(o);
      }

      @Override
      public boolean equals(Object obj) {
        if (!(obj instanceof SubjectRef that)) {
          return false;
        }
        return Objects.equals(this.kind(), that.kind()) && Objects.equals(this.id(), that.id());
      }

      @Override
      public String toString() {
        if (o == null) {
          return "";
        }

        return o.toString();
      }
    };
  }
}

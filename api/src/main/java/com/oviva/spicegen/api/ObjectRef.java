package com.oviva.spicegen.api;

public interface ObjectRef {
  String kind();

  String id();

  static ObjectRef of(String kind, String id) {
    return new ObjectRef() {
      @Override
      public String kind() {
        return kind;
      }

      @Override
      public String id() {
        return id;
      }
    };
  }
}

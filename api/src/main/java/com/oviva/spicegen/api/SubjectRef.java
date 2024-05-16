package com.oviva.spicegen.api;

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
    };
  }
}

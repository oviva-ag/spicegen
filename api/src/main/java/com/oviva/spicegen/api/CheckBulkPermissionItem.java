package com.oviva.spicegen.api;

public interface CheckBulkPermissionItem {

  static Builder newBuilder() {
    return null;
  }

  ObjectRef resource();

  String permission();

  SubjectRef subject();

  interface Builder {
    Builder resource(ObjectRef resource);

    Builder permission(String permission);

    Builder subject(SubjectRef subject);

    CheckBulkPermissionItem build();
  }
}

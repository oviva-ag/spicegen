package com.oviva.spicegen.api;

public interface CheckBulkPermissionsResult {
  boolean permissionGranted();

  CheckPermission request();

  default String permission() {
    return request().permission();
  }

  default ObjectRef resource() {
    return request().resource();
  }

  default SubjectRef subject() {
    return request().subject();
  }
}

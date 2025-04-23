package com.oviva.spicegen.api;

public interface CheckPermissionsResult {
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

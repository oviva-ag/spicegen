package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.CheckPermissionsImpl;
import java.util.List;

public interface CheckPermissions {

  static Builder newBuilder() {
    return CheckPermissionsImpl.newBuilder();
  }

  List<CheckPermission> checkPermissions();

  interface Builder {
    Builder checkPermission(CheckPermission checkPermission);

    Builder checkPermissions(List<CheckPermission> checkPermissions);

    CheckPermissions build();
  }
}

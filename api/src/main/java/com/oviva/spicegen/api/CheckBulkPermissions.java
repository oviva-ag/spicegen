package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.CheckBulkPermissionsImpl;
import java.util.List;

public interface CheckBulkPermissions {

  static Builder newBuilder() {
    return new CheckBulkPermissionsImpl.Builder();
  }

  List<CheckBulkPermissionItem> items();

  Consistency consistency();

  interface Builder {

    Builder items(List<CheckBulkPermissionItem> items);

    Builder consistency(Consistency consistency);

    CheckBulkPermissions build();
  }
}

package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.*;
import java.util.List;

public record CheckBulkPermissionsImpl(List<CheckBulkPermissionItem> items, Consistency consistency)
    implements CheckBulkPermissions {

  private CheckBulkPermissionsImpl(Builder builder) {
    this(builder.items, builder.consistency);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements CheckBulkPermissions.Builder {
    private List<CheckBulkPermissionItem> items = List.of();
    private Consistency consistency = Consistency.fullyConsistent();

    @Override
    public CheckBulkPermissions.Builder items(List<CheckBulkPermissionItem> items) {
      this.items = items;
      return this;
    }

    @Override
    public Builder consistency(Consistency consistency) {
      this.consistency = consistency;
      return this;
    }

    @Override
    public CheckBulkPermissions build() {
      return new CheckBulkPermissionsImpl(this);
    }
  }
}

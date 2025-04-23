package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.CheckPermission;
import com.oviva.spicegen.api.CheckPermissions;
import java.util.ArrayList;
import java.util.List;

public record CheckPermissionsImpl(List<CheckPermission> checkPermissions)
    implements CheckPermissions {

  private CheckPermissionsImpl(Builder builder) {
    this(builder.checkPermissions);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements CheckPermissions.Builder {
    private List<CheckPermission> checkPermissions = new ArrayList<>();

    @Override
    public Builder checkPermission(CheckPermission checkPermission) {
      this.checkPermissions.add(checkPermission);
      return this;
    }

    @Override
    public Builder checkPermissions(List<CheckPermission> checkPermissions) {
      this.checkPermissions = checkPermissions;
      return this;
    }

    @Override
    public CheckPermissions build() {
      return new CheckPermissionsImpl(this);
    }
  }
}

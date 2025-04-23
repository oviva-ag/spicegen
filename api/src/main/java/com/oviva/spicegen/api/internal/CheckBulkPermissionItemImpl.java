package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.*;

public record CheckBulkPermissionItemImpl(ObjectRef resource, String permission, SubjectRef subject)
    implements CheckBulkPermissionItem {

  private CheckBulkPermissionItemImpl(Builder builder) {
    this(builder.resource, builder.permission, builder.subject);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements CheckBulkPermissionItem.Builder {
    private ObjectRef resource;
    private String permission;
    private SubjectRef subject;

    @Override
    public Builder resource(ObjectRef resource) {
      this.resource = resource;
      return this;
    }

    @Override
    public Builder permission(String permission) {
      this.permission = permission;
      return this;
    }

    @Override
    public Builder subject(SubjectRef subject) {
      this.subject = subject;
      return this;
    }

    @Override
    public CheckBulkPermissionItem build() {
      return new CheckBulkPermissionItemImpl(this);
    }
  }
}

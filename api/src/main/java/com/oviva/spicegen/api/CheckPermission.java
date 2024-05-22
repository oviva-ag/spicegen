package com.oviva.spicegen.api;

import java.util.Objects;

public final class CheckPermission {

  private final ObjectRef resource;
  private final String permission;
  private final SubjectRef subject;

  private final Consistency consistency;

  private CheckPermission(Builder builder) {
    resource = builder.resource;
    permission = builder.permission;
    subject = builder.subject;
    consistency = builder.consistency;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public ObjectRef resource() {
    return resource;
  }

  public String permission() {
    return permission;
  }

  public SubjectRef subject() {
    return subject;
  }

  public Consistency consistency() {
    return consistency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (CheckPermission) o;
    return Objects.equals(resource, that.resource)
        && Objects.equals(permission, that.permission)
        && Objects.equals(subject, that.subject)
        && Objects.equals(consistency, that.consistency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, permission, subject, consistency);
  }

  public static final class Builder {
    private ObjectRef resource;
    private String permission;
    private SubjectRef subject;
    private Consistency consistency = Consistency.fullyConsistent();

    private Builder() {}

    public Builder resource(ObjectRef resource) {
      this.resource = resource;
      return this;
    }

    public Builder permission(String permission) {
      this.permission = permission;
      return this;
    }

    public Builder subject(SubjectRef subject) {
      this.subject = subject;
      return this;
    }

    public Builder consistency(Consistency consistency) {
      this.consistency = consistency;
      return this;
    }

    public CheckPermission build() {
      return new CheckPermission(this);
    }
  }
}

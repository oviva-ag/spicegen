package com.oviva.spicegen;

import com.oviva.spicegen.generator.ObjectRef;
import java.util.Objects;

public final class CheckPermission {

  private final ObjectRef resource;
  private final String permission;
  private final ObjectRef subject;

  private final String consistencyToken;

  private CheckPermission(Builder builder) {
    resource = builder.resource;
    permission = builder.permission;
    subject = builder.subject;
    consistencyToken = builder.consistencyToken;
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

  public ObjectRef subject() {
    return subject;
  }

  public String consistencyToken() {
    return consistencyToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckPermission that = (CheckPermission) o;
    return Objects.equals(resource, that.resource)
        && Objects.equals(permission, that.permission)
        && Objects.equals(subject, that.subject)
        && Objects.equals(consistencyToken, that.consistencyToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, permission, subject, consistencyToken);
  }

  public static final class Builder {
    private ObjectRef resource;
    private String permission;
    private ObjectRef subject;
    private String consistencyToken;

    private Builder() {}

    public Builder withResource(ObjectRef val) {
      resource = val;
      return this;
    }

    public Builder withPermission(String val) {
      permission = val;
      return this;
    }

    public Builder withSubject(ObjectRef val) {
      subject = val;
      return this;
    }

    public Builder withConsistencyToken(String val) {
      consistencyToken = val;
      return this;
    }

    public CheckPermission build() {
      return new CheckPermission(this);
    }
  }
}

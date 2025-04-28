package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.LookupSubjects;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.ObjectRefFactory;

public record LookupSubjectsImpl<T extends ObjectRef>(
    String permission, ObjectRef resource, ObjectRefFactory<T> subjectType, String subjectRelation)
    implements LookupSubjects<T> {

  private LookupSubjectsImpl(Builder<T> builder) {
    this(builder.permission, builder.resource, builder.subjectType, builder.subjectRelation);
  }

  public static <T extends ObjectRef> Builder<T> newBuilder() {
    return new Builder<>();
  }

  public static final class Builder<T extends ObjectRef> implements LookupSubjects.Builder<T> {
    private String permission;
    private ObjectRef resource;
    private ObjectRefFactory<T> subjectType;
    private String subjectRelation;

    @Override
    public Builder<T> permission(String permission) {
      this.permission = permission;
      return this;
    }

    @Override
    public Builder<T> resource(ObjectRef resource) {
      this.resource = resource;
      return this;
    }

    @Override
    public Builder<T> subjectType(ObjectRefFactory<T> subjectType) {
      this.subjectType = subjectType;
      return this;
    }

    @Override
    public LookupSubjects.Builder<T> subjectRelation(String subjectRelation) {
      this.subjectRelation = subjectRelation;
      return this;
    }

    @Override
    public LookupSubjects<T> build() {
      if (permission == null || resource == null || subjectType == null) {
        throw new IllegalStateException("Permission, resource, and subjectType must be set");
      }
      return new LookupSubjectsImpl<>(this);
    }
  }
}

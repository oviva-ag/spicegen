package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.LookupSuspectsImpl;

public interface LookupSuspects<T extends ObjectRef> {

  String permission();

  ObjectRef resource();

  ObjectRefFactory<T> subjectType();

  String subjectRelation();

  static <T extends ObjectRef> Builder<T> newBuilder() {
    return LookupSuspectsImpl.newBuilder();
  }

  interface Builder<T extends ObjectRef> {
    Builder<T> permission(String permission);

    Builder<T> resource(ObjectRef resource);

    Builder<T> subjectType(ObjectRefFactory<T> subjectType);

    Builder<T> subjectRelation(String subjectRelation);

    LookupSuspects<T> build();
  }
}

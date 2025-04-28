package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.LookupSubjectsImpl;

public interface LookupSubjects<T extends ObjectRef> {

  String permission();

  ObjectRef resource();

  ObjectRefFactory<T> subjectType();

  String subjectRelation();

  static <T extends ObjectRef> Builder<T> newBuilder() {
    return LookupSubjectsImpl.newBuilder();
  }

  interface Builder<T extends ObjectRef> {
    Builder<T> permission(String permission);

    Builder<T> resource(ObjectRef resource);

    Builder<T> subjectType(ObjectRefFactory<T> subjectType);

    Builder<T> subjectRelation(String subjectRelation);

    LookupSubjects<T> build();
  }
}

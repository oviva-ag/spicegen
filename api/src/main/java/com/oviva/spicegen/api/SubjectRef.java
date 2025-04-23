package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.SubjectRefImpl;

public interface SubjectRef {
  String kind();

  String id();

  String relation();

  static SubjectRef ofObject(ObjectRef o) {
    if (o == null) {
      return null;
    }
    return new SubjectRefImpl(o.kind(), o.id());
  }

  static SubjectRef ofObjectWithRelation(ObjectRef o, String relation) {
    if (o == null) {
      return null;
    }
    return new SubjectRefImpl(o.kind(), o.id(), relation);
  }
}

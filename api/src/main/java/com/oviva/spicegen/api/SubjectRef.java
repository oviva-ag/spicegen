package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.SubjectRefImpl;

public interface SubjectRef {
  String kind();

  String id();

  static SubjectRef ofObject(ObjectRef o) {
    if (o == null) {
      return null;
    }
    return new SubjectRefImpl(o.kind(), o.id());
  }
}

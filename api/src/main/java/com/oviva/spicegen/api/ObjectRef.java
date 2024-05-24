package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.ObjectRefImpl;

public interface ObjectRef {
  String kind();

  String id();

  static ObjectRef of(String kind, String id) {
    if (kind == null) {
      throw new IllegalArgumentException("kind must  not be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("id must  not be null");
    }

    return new ObjectRefImpl(kind, id);
  }
}

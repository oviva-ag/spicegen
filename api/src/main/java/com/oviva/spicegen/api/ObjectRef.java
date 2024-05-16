package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.SimpleObjectRef;

public interface ObjectRef {
  String kind();

  String id();

  static ObjectRef of(String kind, String id) {
    return SimpleObjectRef.of(kind, id);
  }
}

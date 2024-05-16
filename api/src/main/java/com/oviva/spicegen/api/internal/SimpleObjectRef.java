package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.ObjectRef;

public final class SimpleObjectRef implements ObjectRef {
  private final String kind;
  private final String id;

  public static ObjectRef of(String kind, String id) {
    return new SimpleObjectRef(kind, id);
  }

  private SimpleObjectRef(String kind, String id) {
    this.kind = kind;
    this.id = id;
  }

  @Override
  public String kind() {
    return kind;
  }

  @Override
  public String id() {
    return id;
  }
}

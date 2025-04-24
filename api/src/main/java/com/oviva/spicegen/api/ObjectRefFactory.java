package com.oviva.spicegen.api;

public interface ObjectRefFactory<T extends ObjectRef> {
  Class<T> getRefClass();

  T create(String id);

  String kind();
}

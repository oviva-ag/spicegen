package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.SubjectRef;

public record SubjectRefImpl(String kind, String id) implements SubjectRef {
  @Override
  public String toString() {
    return "%s:%s".formatted(kind, id);
  }
}

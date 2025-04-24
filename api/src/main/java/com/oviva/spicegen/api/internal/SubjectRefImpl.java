package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.SubjectRef;

public record SubjectRefImpl(String kind, String id, String relation) implements SubjectRef {
  public SubjectRefImpl(String kind, String id) {
    this(kind, id, null);
  }

  @Override
  public String toString() {
    if (relation == null || relation.isEmpty()) {
      return "%s:%s".formatted(kind, id);
    } else {
      return "%s:%s#%s".formatted(kind, id, relation);
    }
  }
}

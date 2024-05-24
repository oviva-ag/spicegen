package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.ConsistencyImpl;

public interface Consistency {

  String consistencyToken();

  Requirement requirement();

  static Consistency fullyConsistent() {
    return ConsistencyImpl.fullyConsistent();
  }

  static Consistency atLeastAsFreshAs(String consistencyToken) {
    if (consistencyToken == null) {
      return ConsistencyImpl.fullyConsistent();
    }
    return new ConsistencyImpl(Consistency.Requirement.AT_LEAST_AS_FRESH, consistencyToken);
  }

  enum Requirement {
    FULLY_CONSISTENT,
    AT_LEAST_AS_FRESH
  }
}

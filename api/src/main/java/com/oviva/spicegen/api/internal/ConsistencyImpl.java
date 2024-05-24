package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Consistency;

public record ConsistencyImpl(Consistency.Requirement requirement, String consistencyToken)
    implements Consistency {

  private static final ConsistencyImpl FULLY_CONSISTENT =
      new ConsistencyImpl(Consistency.Requirement.FULLY_CONSISTENT, null);

  public static ConsistencyImpl fullyConsistent() {
    return FULLY_CONSISTENT;
  }
}

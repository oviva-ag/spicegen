package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Consistency;

public record ConsistencyImpl(Consistency.Requirement requirement, String consistencyToken)
    implements Consistency {

  private static final ConsistencyImpl FULLY_CONSISTENT =
      new ConsistencyImpl(Consistency.Requirement.FULLY_CONSISTENT, null);

  private static final ConsistencyImpl MINIMIZE_LATENCY =
      new ConsistencyImpl(Consistency.Requirement.MINIMIZE_LATENCY, null);

  public static ConsistencyImpl fullyConsistent() {
    return FULLY_CONSISTENT;
  }

  public static ConsistencyImpl minimizeLatency() {
    return MINIMIZE_LATENCY;
  }
}

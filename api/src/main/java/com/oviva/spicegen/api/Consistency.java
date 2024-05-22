package com.oviva.spicegen.api;

import java.util.Objects;

public final class Consistency {

  private static Consistency FULLY_CONSISTENT = new Consistency(Requirement.FULLY_CONSISTENT, null);
  private final Requirement requirement;

  private final String consistencyToken;

  private Consistency(Requirement requirement, String consistencyToken) {
    this.requirement = requirement;
    this.consistencyToken = consistencyToken;
  }

  public String consistencyToken() {
    return consistencyToken;
  }

  public Requirement requirement() {
    return requirement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (Consistency) o;
    return requirement == that.requirement
        && Objects.equals(consistencyToken, that.consistencyToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requirement, consistencyToken);
  }

  public static Consistency fullyConsistent() {
    return FULLY_CONSISTENT;
  }

  public static Consistency atLeastAsFreshAs(String consistencyToken) {
    return new Consistency(Requirement.AT_LEAST_AS_FRESH, consistencyToken);
  }

  public enum Requirement {
    FULLY_CONSISTENT,
    AT_LEAST_AS_FRESH
  }
}

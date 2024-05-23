package com.oviva.spicegen.api;

public final class Precondition {

  private final Condition condition;
  private final RelationshipFilter filter;

  private Precondition(Builder builder) {
    condition = builder.condition;
    filter = builder.filter;
  }

  public static Precondition mustMatch(RelationshipFilter filter) {
    return new Precondition(Condition.MUST_MATCH, filter);
  }

  public static Precondition mustNotMatch(RelationshipFilter filter) {
    return new Precondition(Condition.MUST_NOT_MATCH, filter);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private Precondition(Condition condition, RelationshipFilter filter) {
    this.condition = condition;
    this.filter = filter;
  }

  public Condition condition() {
    return condition;
  }

  public RelationshipFilter filter() {
    return filter;
  }

  public enum Condition {
    MUST_MATCH,
    MUST_NOT_MATCH
  }

  public static final class Builder {
    private Condition condition;
    private RelationshipFilter filter;

    private Builder() {}

    public Builder condition(Condition val) {
      condition = val;
      return this;
    }

    public Builder filter(RelationshipFilter val) {
      filter = val;
      return this;
    }

    public Precondition build() {
      return new Precondition(this);
    }
  }
}

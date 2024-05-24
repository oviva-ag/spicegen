package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.PreconditionImpl;

public interface Precondition {

  Condition condition();

  RelationshipFilter filter();

  static Precondition mustMatch(RelationshipFilter filter) {
    return new PreconditionImpl(Condition.MUST_MATCH, filter);
  }

  static Precondition mustNotMatch(RelationshipFilter filter) {
    return new PreconditionImpl(Condition.MUST_NOT_MATCH, filter);
  }

  enum Condition {
    MUST_MATCH,
    MUST_NOT_MATCH
  }

  static Builder newBuilder() {
    return new PreconditionImpl.Builder();
  }

  interface Builder {

    Precondition.Builder condition(Condition condition);

    Precondition.Builder filter(RelationshipFilter filter);

    Precondition build();
  }
}

package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Precondition;
import com.oviva.spicegen.api.RelationshipFilter;

public record PreconditionImpl(Precondition.Condition condition, RelationshipFilter filter)
    implements Precondition {

  private PreconditionImpl(Builder builder) {
    this(builder.condition, builder.filter);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements Precondition.Builder {
    private Precondition.Condition condition;
    private RelationshipFilter filter;

    @Override
    public Builder condition(Precondition.Condition val) {
      condition = val;
      return this;
    }

    @Override
    public Builder filter(RelationshipFilter val) {
      filter = val;
      return this;
    }

    public PreconditionImpl build() {
      return new PreconditionImpl(this);
    }
  }
}

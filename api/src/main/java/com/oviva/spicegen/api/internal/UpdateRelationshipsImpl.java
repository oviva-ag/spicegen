package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Precondition;
import com.oviva.spicegen.api.UpdateRelationship;
import com.oviva.spicegen.api.UpdateRelationships;
import java.util.ArrayList;
import java.util.List;

public record UpdateRelationshipsImpl(
    List<Precondition> preconditions, List<UpdateRelationship> updates)
    implements UpdateRelationships {

  public static Builder newBuilder() {
    return new Builder();
  }

  public List<UpdateRelationship> updates() {
    return updates;
  }

  public List<Precondition> preconditions() {
    return preconditions;
  }

  public static final class Builder implements UpdateRelationships.Builder {
    private List<Precondition> preconditions = new ArrayList<>();
    private List<UpdateRelationship> updates = new ArrayList<>();

    @Override
    public UpdateRelationships.Builder preconditions(List<Precondition> preconditions) {
      this.preconditions = preconditions;
      return this;
    }

    @Override
    public UpdateRelationships.Builder precondition(Precondition precondition) {
      this.preconditions.add(precondition);
      return this;
    }

    @Override
    public UpdateRelationships.Builder updates(List<UpdateRelationship> updates) {
      this.updates = updates;
      return this;
    }

    @Override
    public UpdateRelationships.Builder update(UpdateRelationship updates) {
      this.updates.add(updates);
      return this;
    }

    @Override
    public UpdateRelationships build() {
      return new UpdateRelationshipsImpl(preconditions, updates);
    }
  }
}

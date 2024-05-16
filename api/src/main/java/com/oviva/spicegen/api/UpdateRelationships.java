package com.oviva.spicegen.api;

import java.util.ArrayList;
import java.util.List;

public final class UpdateRelationships {

  private final List<Precondition> preconditions;
  private final List<UpdateRelationship> updates;

  public static UpdateRelationshipsBuilder newBuilder() {
    return new UpdateRelationshipsBuilder();
  }

  private UpdateRelationships(List<UpdateRelationship> updates, List<Precondition> preconditions) {
    this.updates = List.copyOf(updates);
    this.preconditions = List.copyOf(preconditions);
  }

  public List<UpdateRelationship> updates() {
    return updates;
  }

  public List<Precondition> preconditions() {
    return preconditions;
  }

  public static final class UpdateRelationshipsBuilder {
    private List<Precondition> preconditions = new ArrayList<>();
    private List<UpdateRelationship> updates = new ArrayList<>();

    private UpdateRelationshipsBuilder() {}

    public static UpdateRelationshipsBuilder newBuilder() {
      return new UpdateRelationshipsBuilder();
    }

    public UpdateRelationshipsBuilder preconditions(List<Precondition> preconditions) {
      this.preconditions = preconditions;
      return this;
    }

    public UpdateRelationshipsBuilder precondition(Precondition precondition) {
      this.preconditions.add(precondition);
      return this;
    }

    public UpdateRelationshipsBuilder updates(List<UpdateRelationship> updates) {
      this.updates = updates;
      return this;
    }

    public UpdateRelationshipsBuilder update(UpdateRelationship updates) {
      this.updates.add(updates);
      return this;
    }

    public UpdateRelationships build() {
      return new UpdateRelationships(updates, preconditions);
    }
  }
}

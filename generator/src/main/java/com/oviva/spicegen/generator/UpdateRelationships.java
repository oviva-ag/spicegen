package com.oviva.spicegen.generator;

import java.util.ArrayList;
import java.util.List;

public final class UpdateRelationships {

  private final List<UpdateRelationship> updates;

  public static UpdateRelationshipsBuilder newBuilder() {
    return new UpdateRelationshipsBuilder();
  }

  private UpdateRelationships(List<UpdateRelationship> updates) {
    this.updates = List.copyOf(updates);
  }

  //  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
  public List<UpdateRelationship> updates() {
    return updates;
  }

  public static final class UpdateRelationshipsBuilder {
    private List<UpdateRelationship> updates = new ArrayList<>();

    private UpdateRelationshipsBuilder() {}

    public static UpdateRelationshipsBuilder newBuilder() {
      return new UpdateRelationshipsBuilder();
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
      return new UpdateRelationships(updates);
    }
  }
}

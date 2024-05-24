package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.UpdateRelationshipsImpl;
import java.util.List;

public interface UpdateRelationships {

  static Builder newBuilder() {
    return new UpdateRelationshipsImpl.Builder();
  }

  List<UpdateRelationship> updates();

  List<Precondition> preconditions();

  interface Builder {
    Builder preconditions(List<Precondition> preconditions);

    Builder precondition(Precondition precondition);

    Builder updates(List<UpdateRelationship> updates);

    Builder update(UpdateRelationship updates);

    UpdateRelationships build();
  }
}

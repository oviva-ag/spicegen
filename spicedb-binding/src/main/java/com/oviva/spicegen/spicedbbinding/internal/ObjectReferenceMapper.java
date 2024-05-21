package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.Core;
import com.oviva.spicegen.api.ObjectRef;

public class ObjectReferenceMapper {

  public Core.ObjectReference map(ObjectRef ref) {
    return Core.ObjectReference.newBuilder()
        .setObjectType(ref.kind())
        .setObjectId(ref.id())
        .build();
  }
}

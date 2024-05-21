package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.Core;
import com.oviva.spicegen.api.SubjectRef;

public class SubjectReferenceMapper {

  public Core.SubjectReference map(SubjectRef subjectRef) {
    var ref =
        Core.ObjectReference.newBuilder()
            .setObjectType(subjectRef.kind())
            .setObjectId(subjectRef.id())
            .build();
    return Core.SubjectReference.newBuilder().setObject(ref).build();
  }
}

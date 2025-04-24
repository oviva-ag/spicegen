package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.ObjectReference;
import com.authzed.api.v1.SubjectReference;
import com.oviva.spicegen.api.SubjectRef;

public class SubjectReferenceMapper {

  public SubjectReference map(SubjectRef subjectRef) {
    var ref =
        ObjectReference.newBuilder()
            .setObjectType(subjectRef.kind())
            .setObjectId(subjectRef.id())
            .build();
    var builder = SubjectReference.newBuilder().setObject(ref);
    if (subjectRef.relation() != null) {
      builder.setOptionalRelation(subjectRef.relation());
    }
    return builder.build();
  }
}

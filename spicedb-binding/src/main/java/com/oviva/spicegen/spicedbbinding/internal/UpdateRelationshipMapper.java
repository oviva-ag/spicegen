package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.Core;
import com.oviva.spicegen.api.UpdateRelationship;

public class UpdateRelationshipMapper {

  private final ObjectReferenceMapper objectReferenceMapper = new ObjectReferenceMapper();
  private final SubjectReferenceMapper subjectReferenceMapper = new SubjectReferenceMapper();

  public Core.RelationshipUpdate map(UpdateRelationship updateRelationship) {

    var subjectRef = subjectReferenceMapper.map(updateRelationship.subject());
    var resourceRef = objectReferenceMapper.map(updateRelationship.resource());

    return Core.RelationshipUpdate.newBuilder()
        .setOperation(mapOperation(updateRelationship.operation()))
        .setRelationship(
            Core.Relationship.newBuilder()
                .setRelation(updateRelationship.relation())
                .setSubject(subjectRef)
                .setResource(resourceRef))
        .build();
  }

  private Core.RelationshipUpdate.Operation mapOperation(UpdateRelationship.Operation operation) {
    return switch (operation) {
      case UPDATE -> Core.RelationshipUpdate.Operation.OPERATION_TOUCH;
      case DELETE -> Core.RelationshipUpdate.Operation.OPERATION_DELETE;
    };
  }
}

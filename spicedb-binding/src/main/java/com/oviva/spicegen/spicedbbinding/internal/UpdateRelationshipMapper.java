package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.Relationship;
import com.authzed.api.v1.RelationshipUpdate;
import com.oviva.spicegen.api.UpdateRelationship;

public class UpdateRelationshipMapper {

  private final ObjectReferenceMapper objectReferenceMapper = new ObjectReferenceMapper();
  private final SubjectReferenceMapper subjectReferenceMapper = new SubjectReferenceMapper();

  public RelationshipUpdate map(UpdateRelationship updateRelationship) {

    var subjectRef = subjectReferenceMapper.map(updateRelationship.subject());
    var resourceRef = objectReferenceMapper.map(updateRelationship.resource());

    return RelationshipUpdate.newBuilder()
        .setOperation(mapOperation(updateRelationship.operation()))
        .setRelationship(
            Relationship.newBuilder()
                .setRelation(updateRelationship.relation())
                .setSubject(subjectRef)
                .setResource(resourceRef))
        .build();
  }

  private RelationshipUpdate.Operation mapOperation(UpdateRelationship.Operation operation) {
    return switch (operation) {
      case UPDATE -> RelationshipUpdate.Operation.OPERATION_TOUCH;
      case DELETE -> RelationshipUpdate.Operation.OPERATION_DELETE;
    };
  }
}

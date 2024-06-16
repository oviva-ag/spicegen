package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.SubjectRef;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiceDbUtils {

  private static Logger logger = LoggerFactory.getLogger(SpiceDbUtils.class);

  public static String newId() {
    // removing `-` to avoid problems on the playground
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  public static ObjectReference toRef(ObjectRef ref) {

    return ObjectReference.newBuilder().setObjectType(ref.kind()).setObjectId(ref.id()).build();
  }

  public static SubjectReference toRef(SubjectRef ref) {

    return SubjectReference.newBuilder()
        .setObject(ObjectReference.newBuilder().setObjectId(ref.id()).setObjectType(ref.kind()))
        .build();
  }

  public static WriteRelationshipsRequest updateRelationshipRequest(
      ObjectRef resource, String relation, ObjectRef subject) {

    logger.info("update: " + resource.toString() + "#" + relation + "@" + subject);
    return writeRelationshipRequest(
        resource, relation, subject, RelationshipUpdate.Operation.OPERATION_TOUCH);
  }

  public static WriteRelationshipsRequest deleteRelationshipRequest(
      ObjectRef resource, String relation, ObjectRef subject) {

    logger.info("delete: " + resource.toString() + "#" + relation + "@" + subject);
    return writeRelationshipRequest(
        resource, relation, subject, RelationshipUpdate.Operation.OPERATION_DELETE);
  }

  public static WriteRelationshipsRequest writeRelationshipRequest(
      ObjectRef resource,
      String relation,
      ObjectRef subject,
      RelationshipUpdate.Operation operation) {

    logger.info("write: " + resource.toString() + "#" + relation + "@" + subject);

    var subjectRef = toRef(subject);

    var resourceRef = toRef(resource);

    return WriteRelationshipsRequest.newBuilder()
        .addUpdates(
            RelationshipUpdate.newBuilder()
                .setOperation(operation)
                .setRelationship(
                    Relationship.newBuilder()
                        .setRelation(relation)
                        .setSubject(SubjectReference.newBuilder().setObject(subjectRef).build())
                        .setResource(resourceRef))
                .build())
        .build();
  }

  public static WriteSchemaRequest writeSchemaRequest(String schema) {
    return WriteSchemaRequest.newBuilder().setSchema(schema).build();
  }

  public static CheckPermissionRequest checkPermissionRequest(
      ObjectRef resource, String permission, SubjectRef subject) {

    return CheckPermissionRequest.newBuilder()
        .setConsistency(Consistency.newBuilder().setFullyConsistent(true).build())
        .setResource(toRef(resource))
        .setPermission(permission)
        .setSubject(toRef(subject))
        .build();
  }

  public static LookupResourcesRequest lookupResourcesRequest(
      String resourceType, String permission, SubjectRef subject) {

    return LookupResourcesRequest.newBuilder()
        .setConsistency(Consistency.newBuilder().setFullyConsistent(true).build())
        .setResourceObjectType(resourceType)
        .setPermission(permission)
        .setSubject(toRef(subject))
        .build();
  }
}

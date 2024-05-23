package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.Core;
import com.authzed.api.v1.PermissionService;
import com.authzed.api.v1.SchemaServiceOuterClass;
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

  public static Core.ObjectReference toRef(ObjectRef ref) {

    return Core.ObjectReference.newBuilder()
        .setObjectType(ref.kind())
        .setObjectId(ref.id())
        .build();
  }

  public static Core.SubjectReference toRef(SubjectRef ref) {

    return Core.SubjectReference.newBuilder()
        .setObject(
            Core.ObjectReference.newBuilder().setObjectId(ref.id()).setObjectType(ref.kind()))
        .build();
  }

  public static PermissionService.WriteRelationshipsRequest updateRelationshipRequest(
      ObjectRef resource, String relation, ObjectRef subject) {

    logger.info("update: " + resource.toString() + "#" + relation + "@" + subject);
    return writeRelationshipRequest(
        resource, relation, subject, Core.RelationshipUpdate.Operation.OPERATION_TOUCH);
  }

  public static PermissionService.WriteRelationshipsRequest deleteRelationshipRequest(
      ObjectRef resource, String relation, ObjectRef subject) {

    logger.info("delete: " + resource.toString() + "#" + relation + "@" + subject);
    return writeRelationshipRequest(
        resource, relation, subject, Core.RelationshipUpdate.Operation.OPERATION_DELETE);
  }

  public static PermissionService.WriteRelationshipsRequest writeRelationshipRequest(
      ObjectRef resource,
      String relation,
      ObjectRef subject,
      Core.RelationshipUpdate.Operation operation) {

    logger.info("write: " + resource.toString() + "#" + relation + "@" + subject);

    var subjectRef = toRef(subject);

    var resourceRef = toRef(resource);

    return PermissionService.WriteRelationshipsRequest.newBuilder()
        .addUpdates(
            Core.RelationshipUpdate.newBuilder()
                .setOperation(operation)
                .setRelationship(
                    Core.Relationship.newBuilder()
                        .setRelation(relation)
                        .setSubject(
                            Core.SubjectReference.newBuilder().setObject(subjectRef).build())
                        .setResource(resourceRef))
                .build())
        .build();
  }

  public static SchemaServiceOuterClass.WriteSchemaRequest writeSchemaRequest(String schema) {
    return SchemaServiceOuterClass.WriteSchemaRequest.newBuilder().setSchema(schema).build();
  }

  public static PermissionService.CheckPermissionRequest checkPermissionRequest(
      ObjectRef resource, String permission, SubjectRef subject) {

    return PermissionService.CheckPermissionRequest.newBuilder()
        .setConsistency(PermissionService.Consistency.newBuilder().setFullyConsistent(true).build())
        .setResource(toRef(resource))
        .setPermission(permission)
        .setSubject(toRef(subject))
        .build();
  }

  public static PermissionService.LookupResourcesRequest lookupResourcesRequest(
      String resourceType, String permission, SubjectRef subject) {

    return PermissionService.LookupResourcesRequest.newBuilder()
        .setConsistency(PermissionService.Consistency.newBuilder().setFullyConsistent(true).build())
        .setResourceObjectType(resourceType)
        .setPermission(permission)
        .setSubject(toRef(subject))
        .build();
  }
}

package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.*;
import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.PermissionService;
import io.grpc.StatusRuntimeException;

public class SpiceDbPermissionServiceImpl implements PermissionService {
  private final UpdateRelationshipMapper updateRelationshipMapper = new UpdateRelationshipMapper();
  private final PreconditionMapper preconditionMapper = new PreconditionMapper();

  private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

  private final GrpcExceptionMapper exceptionMapper = new GrpcExceptionMapper();

  private final ObjectReferenceMapper objectReferenceMapper = new ObjectReferenceMapper();
  private final SubjectReferenceMapper subjectReferenceMapper = new SubjectReferenceMapper();

  public SpiceDbPermissionServiceImpl(
      PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService) {
    this.permissionsService = permissionsService;
  }

  @Override
  public UpdateResult updateRelationships(UpdateRelationships updates) {

    var mappedUpdates = updates.updates().stream().map(updateRelationshipMapper::map).toList();
    var mappedPreconditions =
        updates.preconditions().stream().map(preconditionMapper::map).toList();

    var req =
        WriteRelationshipsRequest.newBuilder()
            .addAllOptionalPreconditions(mappedPreconditions)
            .addAllUpdates(mappedUpdates)
            .build();

    try {
      var res = permissionsService.writeRelationships(req);
      var zedToken = res.getWrittenAt().getToken();
      return new UpdateResultImpl(zedToken);
    } catch (StatusRuntimeException e) {
      throw exceptionMapper.map(e);
    }
  }

  @Override
  public boolean checkPermission(CheckPermission checkPermission) {

    var request = mapCheckPermission(checkPermission);

    try {
      var response = permissionsService.checkPermission(request);
      return response.getPermissionship()
          == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
    } catch (StatusRuntimeException e) {
      throw exceptionMapper.map(e);
    }
  }

  private CheckPermissionRequest mapCheckPermission(CheckPermission checkPermission) {

    var consistency = mapConsistency(checkPermission.consistency());

    return CheckPermissionRequest.newBuilder()
        .setConsistency(consistency)
        .setResource(objectReferenceMapper.map(checkPermission.resource()))
        .setSubject(subjectReferenceMapper.map(checkPermission.subject()))
        .setPermission(checkPermission.permission())
        .build();
  }

  private com.authzed.api.v1.Consistency mapConsistency(Consistency consistency) {
    return switch (consistency.requirement()) {
      case FULLY_CONSISTENT ->
          com.authzed.api.v1.Consistency.newBuilder().setFullyConsistent(true).build();
      case AT_LEAST_AS_FRESH ->
          com.authzed.api.v1.Consistency.newBuilder()
              .setAtLeastAsFresh(
                  ZedToken.newBuilder().setToken(consistency.consistencyToken()).build())
              .build();
    };
  }
}

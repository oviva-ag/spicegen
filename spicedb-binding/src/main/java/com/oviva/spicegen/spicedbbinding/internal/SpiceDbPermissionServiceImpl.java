package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.*;
import com.oviva.spicegen.api.PermissionService;
import io.grpc.StatusRuntimeException;

public class SpiceDbPermissionServiceImpl implements PermissionService {

  private final PreconditionMapper preconditionMapper = new PreconditionMapper();

  private final ObjectReferenceMapper objectReferenceMapper = new ObjectReferenceMapper();
  private final SubjectReferenceMapper subjectReferenceMapper = new SubjectReferenceMapper();
  private final ConsistencyMapper consistencyMapper = new ConsistencyMapper();

  private final UpdateRelationshipMapper updateRelationshipMapper =
      new UpdateRelationshipMapper(objectReferenceMapper, subjectReferenceMapper);

  private final CheckPermissionMapper checkPermissionMapper =
      new CheckPermissionMapper(consistencyMapper, objectReferenceMapper, subjectReferenceMapper);

  private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

  private final GrpcExceptionMapper exceptionMapper = new GrpcExceptionMapper();

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

    var request = checkPermissionMapper.map(checkPermission);

    try {
      var response = permissionsService.checkPermission(request);
      return response.getPermissionship()
          == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
    } catch (StatusRuntimeException e) {
      throw exceptionMapper.map(e);
    }
  }
}

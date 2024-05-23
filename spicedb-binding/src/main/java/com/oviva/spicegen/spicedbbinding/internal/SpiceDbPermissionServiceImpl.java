package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.oviva.spicegen.api.PermissionService;
import com.oviva.spicegen.api.UpdateRelationships;
import com.oviva.spicegen.api.UpdateResult;
import io.grpc.StatusRuntimeException;

public class SpiceDbPermissionServiceImpl implements PermissionService {
  private final CreateRelationshipUpdateToSpiceDBUpdateRelationshipMapper updateRelationshipMapper =
      new CreateRelationshipUpdateToSpiceDBUpdateRelationshipMapper();
  private final PreconditionMapper preconditionMapper = new PreconditionMapper();

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
        com.authzed.api.v1.PermissionService.WriteRelationshipsRequest.newBuilder()
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
}

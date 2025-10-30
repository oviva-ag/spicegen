package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.*;
import com.oviva.spicegen.api.PermissionService;
import com.oviva.spicegen.api.exceptions.ClientException;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

  private final Duration requestTimeout;

  public SpiceDbPermissionServiceImpl(
      PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService,
      Duration requestTimeout) {
    this.permissionsService = permissionsService;
    this.requestTimeout = requestTimeout;
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
      var res = permissionsService.withDeadlineAfter(requestTimeout).writeRelationships(req);
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
      var response = permissionsService.withDeadlineAfter(requestTimeout).checkPermission(request);
      return response.getPermissionship()
          == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
    } catch (StatusRuntimeException e) {
      throw exceptionMapper.map(e);
    }
  }

  @Override
  public List<CheckBulkPermissionsResult> checkBulkPermissions(
      CheckBulkPermissions checkBulkPermissions) {
    var request = checkPermissionMapper.mapBulk(checkBulkPermissions);

    try {
      var response =
          permissionsService.withDeadlineAfter(requestTimeout).checkBulkPermissions(request);
      if (response.getPairsCount() != checkBulkPermissions.items().size()) {
        throw new ClientException("Amount of response pairs does not match request");
      }
      var results = new ArrayList<CheckBulkPermissionsResult>(response.getPairsCount());
      for (var i = 0; i < response.getPairsList().size(); i++) {
        var checkBulkPermissionsPair = response.getPairs(i);
        var permissionGranted =
            checkBulkPermissionsPair.getItem().getPermissionship()
                == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
        results.add(
            new CheckPermissionsResultImpl(permissionGranted, checkBulkPermissions.items().get(i)));
      }
      return results;
    } catch (StatusRuntimeException e) {
      throw exceptionMapper.map(e);
    }
  }
}

package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.CheckBulkPermissionsRequest;
import com.authzed.api.v1.CheckBulkPermissionsRequestItem;
import com.authzed.api.v1.CheckPermissionRequest;
import com.oviva.spicegen.api.CheckBulkPermissions;
import com.oviva.spicegen.api.CheckPermission;

public class CheckPermissionMapper {

  private final ConsistencyMapper consistencyMapper;
  private final ObjectReferenceMapper objectReferenceMapper;
  private final SubjectReferenceMapper subjectReferenceMapper;

  public CheckPermissionMapper(
      ConsistencyMapper consistencyMapper,
      ObjectReferenceMapper objectReferenceMapper,
      SubjectReferenceMapper subjectReferenceMapper) {
    this.consistencyMapper = consistencyMapper;
    this.objectReferenceMapper = objectReferenceMapper;
    this.subjectReferenceMapper = subjectReferenceMapper;
  }

  public CheckPermissionRequest map(CheckPermission checkPermission) {

    var consistency = consistencyMapper.map(checkPermission.consistency());

    return CheckPermissionRequest.newBuilder()
        .setConsistency(consistency)
        .setResource(objectReferenceMapper.map(checkPermission.resource()))
        .setSubject(subjectReferenceMapper.map(checkPermission.subject()))
        .setPermission(checkPermission.permission())
        .build();
  }

  public CheckBulkPermissionsRequest mapBulk(CheckBulkPermissions checkBulkPermissions) {
    // make sure all consistency tokens are the same, or default to fully consistent
    var consistency = consistencyMapper.map(checkBulkPermissions.consistency());
    var requestBuilder = CheckBulkPermissionsRequest.newBuilder().setConsistency(consistency);
    for (var checkPermission : checkBulkPermissions.items()) {
      requestBuilder.addItems(
          CheckBulkPermissionsRequestItem.newBuilder()
              .setResource(objectReferenceMapper.map(checkPermission.resource()))
              .setSubject(subjectReferenceMapper.map(checkPermission.subject()))
              .setPermission(checkPermission.permission()));
    }
    return requestBuilder.build();
  }
}

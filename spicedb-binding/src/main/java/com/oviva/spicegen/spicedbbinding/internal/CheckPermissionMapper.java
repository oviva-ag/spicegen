package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.CheckBulkPermissionsRequest;
import com.authzed.api.v1.CheckBulkPermissionsRequestItem;
import com.authzed.api.v1.CheckPermissionRequest;
import com.oviva.spicegen.api.CheckPermission;
import com.oviva.spicegen.api.Consistency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

  public CheckBulkPermissionsRequest mapBulk(List<CheckPermission> checkPermissions) {
    // make sure all consistency tokens are the same, or default to fully consistent
    var requestBuilder = CheckBulkPermissionsRequest.newBuilder();
    var consistency = Optional.<Consistency>empty();
    for (var checkPermission : checkPermissions) {
      if (consistency.isEmpty()) {
        consistency = Optional.of(checkPermission.consistency());
      }
      if (!Objects.equals(checkPermission.consistency(), consistency.get())) {
        consistency = Optional.of(Consistency.fullyConsistent());
      }
      requestBuilder.addItems(
          CheckBulkPermissionsRequestItem.newBuilder()
              .setResource(objectReferenceMapper.map(checkPermission.resource()))
              .setSubject(subjectReferenceMapper.map(checkPermission.subject()))
              .setPermission(checkPermission.permission()));
    }
    consistency.map(consistencyMapper::map).ifPresent(requestBuilder::setConsistency);
    return requestBuilder.build();
  }
}

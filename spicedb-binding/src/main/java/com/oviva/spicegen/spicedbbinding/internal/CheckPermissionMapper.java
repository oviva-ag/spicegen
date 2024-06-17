package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.CheckPermissionRequest;
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
}

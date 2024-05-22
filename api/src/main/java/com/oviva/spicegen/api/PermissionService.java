package com.oviva.spicegen.api;

public interface PermissionService {

  /**
   * Updates relationships, optionally with preconditions. The returned consistencyToken should be
   * stored alongside the created resource such that the authorization can be done at the given
   * consistency. This vastly improves the performance and allows the system to function even when
   * it is partitioned.
   *
   * @param updateRelationships the request
   * @return the result, containing the consistencyToken
   */
  UpdateResult updateRelationships(UpdateRelationships updateRelationships);

  /**
   * Checks whether a subject has the given permission on a resource
   *
   * @param checkPermission the request
   * @return true it the subject is permitted, false otherwise
   */
  boolean checkPermission(CheckPermission checkPermission);
}

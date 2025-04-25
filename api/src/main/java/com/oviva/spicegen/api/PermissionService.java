package com.oviva.spicegen.api;

import java.util.List;

import java.util.Iterator;

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

  /**
   * This is a batch version of
   *
   * @see PermissionService#checkPermission(CheckPermission)
   * @param checkBulkPermissions the request
   * @return list of results, one for each checkPermission request
   */
  List<CheckBulkPermissionsResult> checkBulkPermissions(CheckBulkPermissions checkBulkPermissions);

  /**
   * Finds all subjects that have the given permission on the given resource. This method allows
   * querying the authorization system for all users/subjects that have access to a specific
   * resource with a specific permission. The results are returned as an iterator to support
   * handling large result sets efficiently.
   *
   * <p>For example, this can be used to:
   *
   * <ul>
   *   <li>Find all users that can read a document
   *   <li>Find all teams that have admin access to a folder
   *   <li>List all users that can modify a resource
   * </ul>
   *
   * @param lookupSuspects the request containing resource, permission and subject type details
   * @return an iterator over the subjects (users/teams etc) that have the specified permission on
   *     the given resource
   */
  <T extends ObjectRef> Iterator<T> lookupSubjects(LookupSuspects<T> lookupSuspects);
}

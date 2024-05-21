package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.Precondition;
import com.oviva.spicegen.api.RelationshipFilter;

public class PreconditionMapper {

  public com.authzed.api.v1.PermissionService.Precondition map(Precondition precondition) {

    var builder = com.authzed.api.v1.PermissionService.Precondition.newBuilder();

    builder.setOperation(mapOperation(precondition.condition()));
    builder.setFilter(mapFilter(precondition.filter()));

    return builder.build();
  }

  private com.authzed.api.v1.PermissionService.RelationshipFilter mapFilter(
      RelationshipFilter filter) {
    var builder = com.authzed.api.v1.PermissionService.RelationshipFilter.newBuilder();

    builder.setResourceType(filter.resourceKind());
    filter.resourceId().ifPresent(builder::setOptionalResourceId);
    filter.relation().ifPresent(builder::setOptionalRelation);

    filter.subjectFilter().map(this::mapSubjectFilter).ifPresent(builder::setOptionalSubjectFilter);

    return builder.build();
  }

  private com.authzed.api.v1.PermissionService.SubjectFilter mapSubjectFilter(
      RelationshipFilter.SubjectFilter subjectFilter) {
    var subjectFilterBuilder =
        com.authzed.api.v1.PermissionService.SubjectFilter.newBuilder()
            .setSubjectType(subjectFilter.subjectKind());

    subjectFilter.subjectId().ifPresent(subjectFilterBuilder::setOptionalSubjectId);
    subjectFilter
        .relation()
        .map(
            r ->
                com.authzed.api.v1.PermissionService.SubjectFilter.RelationFilter.newBuilder()
                    .setRelation(r)
                    .build())
        .ifPresent(subjectFilterBuilder::setOptionalRelation);
    return subjectFilterBuilder.build();
  }

  private com.authzed.api.v1.PermissionService.Precondition.Operation mapOperation(
      Precondition.Condition condition) {
    return switch (condition) {
      case MUST_MATCH ->
          com.authzed.api.v1.PermissionService.Precondition.Operation.OPERATION_MUST_MATCH;
      case MUST_NOT_MATCH ->
          com.authzed.api.v1.PermissionService.Precondition.Operation.OPERATION_MUST_NOT_MATCH;
    };
  }
}

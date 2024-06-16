package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.Precondition;
import com.oviva.spicegen.api.RelationshipFilter;

public class PreconditionMapper {

  public com.authzed.api.v1.Precondition map(Precondition precondition) {

    var builder = com.authzed.api.v1.Precondition.newBuilder();

    builder.setOperation(mapOperation(precondition.condition()));
    builder.setFilter(mapFilter(precondition.filter()));

    return builder.build();
  }

  private com.authzed.api.v1.RelationshipFilter mapFilter(RelationshipFilter filter) {
    var builder = com.authzed.api.v1.RelationshipFilter.newBuilder();

    builder.setResourceType(filter.resourceKind());
    filter.resourceId().ifPresent(builder::setOptionalResourceId);
    filter.relation().ifPresent(builder::setOptionalRelation);

    filter.subjectFilter().map(this::mapSubjectFilter).ifPresent(builder::setOptionalSubjectFilter);

    return builder.build();
  }

  private com.authzed.api.v1.SubjectFilter mapSubjectFilter(
      RelationshipFilter.SubjectFilter subjectFilter) {
    var subjectFilterBuilder =
        com.authzed.api.v1.SubjectFilter.newBuilder().setSubjectType(subjectFilter.subjectKind());

    subjectFilter.subjectId().ifPresent(subjectFilterBuilder::setOptionalSubjectId);
    subjectFilter
        .relation()
        .map(
            r ->
                com.authzed.api.v1.SubjectFilter.RelationFilter.newBuilder().setRelation(r).build())
        .ifPresent(subjectFilterBuilder::setOptionalRelation);
    return subjectFilterBuilder.build();
  }

  private com.authzed.api.v1.Precondition.Operation mapOperation(Precondition.Condition condition) {
    return switch (condition) {
      case MUST_MATCH -> com.authzed.api.v1.Precondition.Operation.OPERATION_MUST_MATCH;
      case MUST_NOT_MATCH -> com.authzed.api.v1.Precondition.Operation.OPERATION_MUST_NOT_MATCH;
    };
  }
}

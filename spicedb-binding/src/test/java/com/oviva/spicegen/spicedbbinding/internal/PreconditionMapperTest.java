package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.oviva.spicegen.api.Precondition;
import com.oviva.spicegen.api.RelationshipFilter;
import org.junit.jupiter.api.Test;

class PreconditionMapperTest {

  @Test
  void map_objectExists() {

    var sut = new PreconditionMapper();

    var relation = "user";
    var userId = "17";

    var precondition =
        Precondition.mustMatch(
            RelationshipFilter.newBuilder().resourceKind(relation).resourceId(userId).build());

    // when
    var mapped = sut.map(precondition);

    // then
    assertEquals(relation, mapped.getFilter().getResourceType());
    assertEquals(userId, mapped.getFilter().getOptionalResourceId());
    assertEquals(
        com.authzed.api.v1.Precondition.Operation.OPERATION_MUST_MATCH, mapped.getOperation());
  }

  @Test
  void map_objectNotExists() {

    var sut = new PreconditionMapper();

    var relation = "user";
    var userId = "77";

    var precondition =
        Precondition.mustNotMatch(
            RelationshipFilter.newBuilder().resourceKind(relation).resourceId(userId).build());

    // when
    var mapped = sut.map(precondition);

    // then
    assertEquals(relation, mapped.getFilter().getResourceType());
    assertEquals(userId, mapped.getFilter().getOptionalResourceId());
    assertEquals(
        com.authzed.api.v1.Precondition.Operation.OPERATION_MUST_NOT_MATCH, mapped.getOperation());
  }

  @Test
  void map_subject() {

    var sut = new PreconditionMapper();

    var relation = "file";
    var fileId = "17";

    var subject = "user";
    var userId = "42";

    var precondition =
        Precondition.mustMatch(
            RelationshipFilter.newBuilder()
                .resourceKind(relation)
                .resourceId(fileId)
                .subjectFilter(
                    RelationshipFilter.SubjectFilter.newBuilder()
                        .subjectKind(subject)
                        .subjectId(userId)
                        .build())
                .build());

    // when
    var mapped = sut.map(precondition);

    // then
    assertEquals(
        com.authzed.api.v1.Precondition.Operation.OPERATION_MUST_MATCH, mapped.getOperation());

    var filter = mapped.getFilter();
    assertEquals(relation, filter.getResourceType());
    assertEquals(fileId, filter.getOptionalResourceId());

    var subFilter = filter.getOptionalSubjectFilter();
    assertEquals(subject, subFilter.getSubjectType());
    assertEquals(userId, subFilter.getOptionalSubjectId());
  }
}

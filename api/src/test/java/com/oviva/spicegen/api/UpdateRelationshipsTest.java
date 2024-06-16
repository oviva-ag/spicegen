package com.oviva.spicegen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UpdateRelationshipsTest {

  private static final String PATIENT_ID = UUID.randomUUID().toString().replaceAll("-", "");
  private static final String TENANT_ID = UUID.randomUUID().toString().replaceAll("-", "");
  private static final String PATIENT = "patient";
  private static final String TENANT = "tenant";
  private static final String USER = "user";
  private static final String ADMINISTRATOR = "administrator";

  @Test
  public void test_updateRelationships_ownBuilder() {

    var updates = getUpdateRelationshipList();
    var preconditions = getPreconditionList();

    var updateRelationships =
        UpdateRelationships.newBuilder().updates(updates).preconditions(preconditions).build();

    assertEquals(updateRelationships.updates(), updates);
    assertEquals(updateRelationships.preconditions(), preconditions);
  }

  @Test
  public void test_updateRelationshipsBuilder() {
    var updates = getUpdateRelationshipList();
    var preconditions = getPreconditionList();

    var updateRelationships =
        UpdateRelationships.newBuilder().updates(updates).preconditions(preconditions).build();

    assertEquals(updateRelationships.updates(), updates);
    assertEquals(updateRelationships.preconditions(), preconditions);
  }

  @Test
  public void test_updateRelationshipsBuilder_withAddOperations() {

    var userId = UUID.randomUUID();
    var subject = ObjectRef.of("user", userId.toString());
    var resource = ObjectRef.of(TENANT, TENANT_ID);

    var precondition =
        Precondition.mustNotMatch(
            RelationshipFilter.newBuilder()
                .resourceKind(TENANT)
                .relation(PATIENT)
                .subjectFilter(
                    RelationshipFilter.SubjectFilter.newBuilder()
                        .subjectKind(USER)
                        .subjectId(userId.toString())
                        .build())
                .build());

    var updateRelationship = UpdateRelationship.ofUpdate(resource, ADMINISTRATOR, subject);

    var updateRelationships =
        UpdateRelationships.newBuilder()
            .precondition(precondition)
            .update(updateRelationship)
            .build();

    assertEquals(1, updateRelationships.updates().size());
    assertEquals(updateRelationships.updates().get(0), updateRelationship);
    assertEquals(1, updateRelationships.preconditions().size());
    assertEquals(updateRelationships.preconditions().get(0), precondition);
  }

  private List<Precondition> getPreconditionList() {

    var subjectFilter =
        RelationshipFilter.SubjectFilter.newBuilder()
            .subjectKind(TENANT)
            .subjectId(TENANT_ID)
            .build();

    var relationshipFilter =
        RelationshipFilter.newBuilder()
            .resourceKind(PATIENT)
            .resourceId(PATIENT_ID)
            .relation(TENANT)
            .subjectFilter(subjectFilter)
            .build();

    var precondition =
        Precondition.newBuilder()
            .condition(Precondition.Condition.MUST_MATCH)
            .filter(relationshipFilter)
            .build();

    return List.of(precondition);
  }

  private List<UpdateRelationship> getUpdateRelationshipList() {
    var id = "9392";

    var resource = ObjectRef.of(TENANT, id);
    var subject = ObjectRef.of(TENANT, id);

    var updateRelationship = UpdateRelationship.ofUpdate(resource, ADMINISTRATOR, subject);

    return List.of(updateRelationship);
  }
}

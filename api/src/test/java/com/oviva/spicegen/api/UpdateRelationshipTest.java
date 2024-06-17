package com.oviva.spicegen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateRelationshipTest {

  private static final String TENANT = "tenant";
  private static final String TENANT_ID = "8288";

  private static final String DOCUMENT = "document";
  private static final String DOCUMENT_ID = "138";
  private static final String ADMINISTRATOR = "administrator";
  private static final String NAMESPACE_ID = "9392";

  @Test
  void test_ofUpdate() {

    var resource = ObjectRef.of(TENANT, NAMESPACE_ID);
    var subject = ObjectRef.of(TENANT, NAMESPACE_ID);

    var updateRelationship = UpdateRelationship.ofUpdate(resource, ADMINISTRATOR, subject);

    assertEquals(UpdateRelationship.Operation.UPDATE, updateRelationship.operation());
    assertEquals(ADMINISTRATOR, updateRelationship.relation());
    assertEquals(updateRelationship.resource(), resource);
    assertEquals(updateRelationship.subject(), SubjectRef.ofObject(subject));
  }

  @Test
  void test_ofDelete() {

    var resource = ObjectRef.of(TENANT, NAMESPACE_ID);
    var subject = ObjectRef.of(TENANT, NAMESPACE_ID);

    var updateRelationship = UpdateRelationship.ofDelete(resource, ADMINISTRATOR, subject);

    assertEquals(UpdateRelationship.Operation.DELETE, updateRelationship.operation());
    assertEquals(ADMINISTRATOR, updateRelationship.relation());
    assertEquals(updateRelationship.resource(), resource);
    assertEquals(updateRelationship.subject(), SubjectRef.ofObject(subject));
  }

  @Test
  void of_hashCode() {
    var h1 =
        UpdateRelationship.ofUpdate(
            ObjectRef.of(DOCUMENT, DOCUMENT_ID), ADMINISTRATOR, ObjectRef.of(TENANT, TENANT_ID));

    var h2 =
        UpdateRelationship.ofUpdate(
            ObjectRef.of(DOCUMENT, DOCUMENT_ID), ADMINISTRATOR, ObjectRef.of(TENANT, TENANT_ID));

    assertEquals(h1.hashCode(), h2.hashCode());
  }

  @Test
  void of_equals_same() {
    var u1 =
        UpdateRelationship.ofUpdate(
            ObjectRef.of(DOCUMENT, DOCUMENT_ID), ADMINISTRATOR, ObjectRef.of(TENANT, TENANT_ID));

    assertEquals(u1, u1);
  }

  @Test
  void of_equals() {
    var uuid = UUID.randomUUID();
    var u1 = createUpdateDocumentOwnedByUser(uuid);
    var u2 = createUpdateDocumentOwnedByUser(uuid);

    assertEquals(u1, u2);
  }

  @Test
  void of_equals_notEqual() {
    var u1 = createUpdateDocumentOwnedByUser(UUID.randomUUID());
    var u2 = createUpdateDocumentOwnedByUser(UUID.randomUUID());

    assertNotEquals(u1, u2);
  }

  @Test
  void of_equals_notEqual_subjectKind() {
    var subjectUuid = UUID.randomUUID();

    var u1 = createUpdateDocumentOwnedByUser(subjectUuid);
    var u2 =
        UpdateRelationship.ofUpdate(
            u1.resource(), u1.relation(), ObjectRef.of("another", u1.subject().id()));

    assertNotEquals(u1, u2);
  }

  @Test
  void of_equals_notEqual_subjectId() {
    var subjectUuid = UUID.randomUUID();

    var u1 = createUpdateDocumentOwnedByUser(subjectUuid);
    var u2 =
        UpdateRelationship.ofUpdate(
            u1.resource(), u1.relation(), ObjectRef.of(u1.subject().kind(), "992982"));

    assertNotEquals(u1, u2);
  }

  @Test
  void of_equals_notEqual_null() {
    var u1 = createUpdateDocumentOwnedByUser(UUID.randomUUID());
    var u2 = (UpdateRelationship) null;

    assertNotEquals(u1, u2);
  }

  @Test
  void of_equals_notEqualOtherClass() {
    var u1 = createUpdateDocumentOwnedByUser(UUID.randomUUID());
    var u2 = new Object();

    assertNotEquals(u1, u2);
  }

  private UpdateRelationship createUpdateDocumentOwnedByUser(UUID subjectUuid) {

    return UpdateRelationship.ofUpdate(
        ObjectRef.of("document", "1"), "owner", ObjectRef.of("user", subjectUuid.toString()));
  }

  @Test
  void of_toString() {
    var obj = ObjectRef.of("document", "4");
    var sub = ObjectRef.of("user", "18");
    var relation = "owner";

    var u = UpdateRelationship.ofUpdate(obj, relation, sub);
    assertEquals("UPDATE(document:4#owner@user:18)", u.toString());
  }

  @Test
  void of_toString_null() {
    var u = UpdateRelationship.ofUpdate(null, null, null);
    assertEquals("UPDATE(#@)", u.toString());
  }
}

package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.SubjectRef;
import com.oviva.spicegen.spicedbbinding.test.Fixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("contract")
class SpiceDbContractTest {

  private SchemaServiceGrpc.SchemaServiceBlockingStub schemaService;
  private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

  @BeforeEach
  void before(
      SchemaServiceGrpc.SchemaServiceBlockingStub schemaService,
      PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService) {

    this.schemaService = schemaService;
    this.permissionsService = permissionsService;
  }

  @TestTemplate
  @ExtendWith(SpiceDbContractTestContextProvider.class)
  void test_patientWithDocument() {

    // given
    writeDocumentSchema();

    var tenantId = SpiceDbUtils.newId();
    var tenant1 = ObjectRef.of("tenant", tenantId);

    var document1 = ObjectRef.of("document", SpiceDbUtils.newId());

    var patientId = SpiceDbUtils.newId();
    var patient1 = ObjectRef.of("patient", patientId);

    var coachJulieId = SpiceDbUtils.newId();
    var julie = ObjectRef.of("user", coachJulieId);

    var coachElsaId = SpiceDbUtils.newId();
    var elsa = ObjectRef.of("user", coachElsaId);

    updateRelationship(tenant1, "coach", julie);
    updateRelationship(tenant1, "coach", elsa);
    updateRelationship(patient1, "tenant", tenant1);

    updateRelationship(patient1, "coacher", julie);

    updateRelationship(document1, "patient", patient1);

    // when & then
    assertTrue(
        checkPermission(
            document1, "write", SubjectRef.ofObject(ObjectRef.of("user", coachJulieId))));
    assertFalse(
        checkPermission(
            document1, "write", SubjectRef.ofObject(ObjectRef.of("user", coachElsaId))));

    assertFalse(
        checkPermission(
            ObjectRef.of("document", "1"),
            "write",
            SubjectRef.ofObject(ObjectRef.of("user", coachElsaId))));
  }

  @TestTemplate
  @ExtendWith(SpiceDbContractTestContextProvider.class)
  void test_permissionUpsert() {

    // given
    writeDocumentSchema();

    var tenantId = SpiceDbUtils.newId();
    var tenant1 = ObjectRef.of("tenant", tenantId);

    var document1 = ObjectRef.of("document", SpiceDbUtils.newId());

    var patientId = SpiceDbUtils.newId();
    var patient1 = ObjectRef.of("patient", patientId);

    var coachJulieId = SpiceDbUtils.newId();
    var julie = ObjectRef.of("user", coachJulieId);

    updateRelationship(tenant1, "coach", julie);
    updateRelationship(patient1, "tenant", tenant1);

    updateRelationship(patient1, "coacher", julie);

    updateRelationship(document1, "patient", patient1);

    // when
    updateRelationship(tenant1, "coach", julie);
    updateRelationship(tenant1, "coach", julie);
    updateRelationship(tenant1, "coach", julie);

    // then
    assertTrue(
        checkPermission(
            document1, "write", SubjectRef.ofObject(ObjectRef.of("user", coachJulieId))));
  }

  @TestTemplate
  @ExtendWith(SpiceDbContractTestContextProvider.class)
  void test_permissionDelete() {

    // given
    writeDocumentSchema();

    var tenantId = SpiceDbUtils.newId();
    var tenant1 = ObjectRef.of("tenant", tenantId);

    var document1 = ObjectRef.of("document", SpiceDbUtils.newId());

    var patientId = SpiceDbUtils.newId();
    var patient1 = ObjectRef.of("patient", patientId);

    var coachJulieId = SpiceDbUtils.newId();
    var julie = ObjectRef.of("user", coachJulieId);

    updateRelationship(tenant1, "coach", julie);
    updateRelationship(patient1, "tenant", tenant1);

    updateRelationship(patient1, "coacher", julie);

    updateRelationship(document1, "patient", patient1);

    // when
    updateRelationship(tenant1, "coach", julie);

    deleteRelationship(tenant1, "coach", julie);
    deleteRelationship(tenant1, "coach", julie);

    // then
    assertFalse(
        checkPermission(
            document1, "write", SubjectRef.ofObject(ObjectRef.of("user", coachJulieId))));
  }

  private void writeDocumentSchema() {

    var schema = Fixtures.contractTestSchema();
    schemaService.writeSchema(SpiceDbUtils.writeSchemaRequest(schema));
  }

  private boolean checkPermission(ObjectRef resource, String permission, SubjectRef subject) {

    var req = SpiceDbUtils.checkPermissionRequest(resource, permission, subject);
    var res = permissionsService.checkPermission(req);
    return res.getPermissionship()
        == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
  }

  private String updateRelationship(ObjectRef resource, String relation, ObjectRef subject) {
    return writeRelationship(
        resource, relation, subject, RelationshipUpdate.Operation.OPERATION_TOUCH);
  }

  private String deleteRelationship(ObjectRef resource, String relation, ObjectRef subject) {

    return writeRelationship(
        resource, relation, subject, RelationshipUpdate.Operation.OPERATION_DELETE);
  }

  private String writeRelationship(
      ObjectRef resource,
      String relation,
      ObjectRef subject,
      RelationshipUpdate.Operation operation) {

    var req = SpiceDbUtils.writeRelationshipRequest(resource, relation, subject, operation);
    var res = permissionsService.writeRelationships(req);
    return res.getWrittenAt().getToken();
  }

  private boolean lookupResources(String resourceType, String permission, SubjectRef subject) {

    var req = SpiceDbUtils.lookupResourcesRequest(resourceType, permission, subject);
    var res = permissionsService.lookupResources(req);
    return res.hasNext();
  }
}

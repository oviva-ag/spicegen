package com.oviva.spicegen.api;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CheckPermissionsTest {

  @Test
  void test_build_success() {

    var namespace = "tenant";
    var id = "9392";
    var consistencyToken = "t0ken";
    var permission = "write";

    var objectRef = ObjectRef.of(namespace, id);
    var subjectRef = SubjectRef.ofObject(ObjectRef.of(namespace, id));

    var checkPermissions =
        CheckPermissions.newBuilder()
            .checkPermissions(List.of(
                CheckPermission.newBuilder()
                    .consistency(Consistency.atLeastAsFreshAs(consistencyToken))
                    .permission(permission)
                    .resource(objectRef)
                    .subject(subjectRef)
                    .build()
            ))
            .build();

    assertEquals(1, checkPermissions.checkPermissions().size());
    CheckPermission firstCheckPermission = checkPermissions.checkPermissions().get(0);
    assertEquals(permission, firstCheckPermission.permission());
    assertEquals(objectRef, checkPermissions.checkPermissions().get(0).resource());
    assertEquals(subjectRef, checkPermissions.checkPermissions().get(0).subject());
  }

  @Test
  void of_hashCode() {
    var h1 = createChecks("17").hashCode();
    var h2 = createChecks("17").hashCode();

    assertEquals(h1, h2);
  }

  private CheckPermissions createChecks(String... subjectId) {

    var checkPermissions = CheckPermissions.newBuilder();

    for (String id : subjectId) {
      checkPermissions
          .checkPermission(CheckPermission.newBuilder()
              .resource(ObjectRef.of("tenant", "1"))
              .subject(SubjectRef.ofObject(ObjectRef.of("user", id)))
              .permission("test")
              .build());
    }

    return checkPermissions.build();
  }

  @Test
  void of_equals_same() {
    var c1 = createChecks("1");

    assertEquals(c1, c1);
  }

  @Test
  void of_equals() {
    var c1 = createChecks("1", "2");
    var c2 = createChecks("1", "2");

    assertEquals(c1, c2);
  }

  @Test
  void of_equals_notEqual() {
    var c1 = createChecks("3", "2");
    var c2 = createChecks("4", "2");

    assertNotEquals(c1, c2);
  }
}

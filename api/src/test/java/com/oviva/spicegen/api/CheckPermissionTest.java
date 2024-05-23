package com.oviva.spicegen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class CheckPermissionTest {

  @Test
  public void test_build_success() {

    var namespace = "tenant";
    var id = "9392";
    var consistencyToken = "t0ken";
    var permission = "write";

    var objectRef = ObjectRef.of(namespace, id);
    var subjectRef = SubjectRef.ofObject(ObjectRef.of(namespace, id));

    var checkPermission =
        CheckPermission.newBuilder()
            .consistency(Consistency.atLeastAsFreshAs(consistencyToken))
            .permission(permission)
            .resource(objectRef)
            .subject(subjectRef)
            .build();

    assertEquals(checkPermission.consistency().consistencyToken(), consistencyToken);
    assertEquals(checkPermission.permission(), permission);
    assertEquals(checkPermission.resource(), objectRef);
    assertEquals(checkPermission.subject(), subjectRef);
  }

  @Test
  void of_hashCode() {
    var h1 = createCheck("17").hashCode();
    var h2 = createCheck("17").hashCode();

    assertEquals(h1, h2);
  }

  private CheckPermission createCheck(String subjectId) {

    return CheckPermission.newBuilder()
        .permission("test")
        .resource(ObjectRef.of("tenant", "1"))
        .subject(SubjectRef.ofObject(ObjectRef.of("user", subjectId)))
        .build();
  }

  @Test
  void of_equals_same() {
    var c1 = createCheck("1");

    assertEquals(c1, c1);
  }

  @Test
  void of_equals() {
    var c1 = createCheck("1");
    var c2 = createCheck("1");

    assertEquals(c1, c2);
  }

  @Test
  void of_equals_notEqual() {
    var c1 = createCheck("3");
    var c2 = createCheck("4");

    assertNotEquals(c1, c2);
  }
}

package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.authzed.api.v1.RelationshipUpdate;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.UpdateRelationship;
import org.junit.jupiter.api.Test;

public class UpdateRelationshipMapperTest {

  private static final String TENANT = "tenant";
  private static final String USER = "user";
  private static final String ADMINISTRATOR = "administrator";
  private static final String ID = "9392";

  private final UpdateRelationshipMapper mapper = new UpdateRelationshipMapper();

  @Test
  public void test_mapper_withUpdateOperation() {

    var resource = ObjectRef.of(TENANT, ID);
    var subject = ObjectRef.of(USER, ID);

    var updateRelationship = UpdateRelationship.ofUpdate(resource, ADMINISTRATOR, subject);
    var map = mapper.map(updateRelationship);

    assertEquals(map.getOperation(), RelationshipUpdate.Operation.OPERATION_TOUCH);
    assertNotNull(map.getRelationship());
    assertEquals(map.getRelationship().getRelation(), ADMINISTRATOR);
    assertEquals(map.getRelationship().getResource().getObjectId(), ID);
    assertEquals(map.getRelationship().getResource().getObjectType(), TENANT);
  }

  @Test
  public void test_mapper_withDeleteOperation() {

    var resource = ObjectRef.of(TENANT, ID);
    var subject = ObjectRef.of(USER, ID);

    var updateRelationship = UpdateRelationship.ofDelete(resource, ADMINISTRATOR, subject);
    var map = mapper.map(updateRelationship);

    assertEquals(map.getOperation(), RelationshipUpdate.Operation.OPERATION_DELETE);
    assertNotNull(map.getRelationship());
    assertEquals(map.getRelationship().getRelation(), ADMINISTRATOR);
    assertEquals(map.getRelationship().getResource().getObjectId(), ID);
    assertEquals(map.getRelationship().getResource().getObjectType(), TENANT);
  }
}

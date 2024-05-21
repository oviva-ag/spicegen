package com.oviva.spicegen.spicedbbinding.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.authzed.api.v1.Core;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.UpdateRelationship;
import org.junit.jupiter.api.Test;

public class CreateRelationshipUpdateToSpiceDBUpdateRelationshipMapperTest {

  private static final String TENANT = "tenant";
  private static final String USER = "user";
  private static final String ADMINISTRATOR = "administrator";
  private static final String ID = "9392";

  private final CreateRelationshipUpdateToSpiceDBUpdateRelationshipMapper mapper =
      new CreateRelationshipUpdateToSpiceDBUpdateRelationshipMapper();

  @Test
  public void test_mapper_withUpdateOperation() {

    var resource = ObjectRef.of(TENANT, ID);
    var subject = ObjectRef.of(USER, ID);

    var updateRelationship = UpdateRelationship.ofUpdate(resource, ADMINISTRATOR, subject);
    var map = mapper.map(updateRelationship);

    assertThat(map.getOperation(), equalTo(Core.RelationshipUpdate.Operation.OPERATION_TOUCH));
    assertThat(map.getRelationship(), notNullValue());
    assertThat(map.getRelationship().getRelation(), equalTo(ADMINISTRATOR));
    assertThat(map.getRelationship().getResource().getObjectId(), equalTo(ID));
    assertThat(map.getRelationship().getResource().getObjectType(), equalTo(TENANT));
  }

  @Test
  public void test_mapper_withDeleteOperation() {

    var resource = ObjectRef.of(TENANT, ID);
    var subject = ObjectRef.of(USER, ID);

    var updateRelationship = UpdateRelationship.ofDelete(resource, ADMINISTRATOR, subject);
    var map = mapper.map(updateRelationship);

    assertThat(map.getOperation(), equalTo(Core.RelationshipUpdate.Operation.OPERATION_DELETE));
    assertThat(map.getRelationship(), notNullValue());
    assertThat(map.getRelationship().getRelation(), equalTo(ADMINISTRATOR));
    assertThat(map.getRelationship().getResource().getObjectId(), equalTo(ID));
    assertThat(map.getRelationship().getResource().getObjectType(), equalTo(TENANT));
  }
}

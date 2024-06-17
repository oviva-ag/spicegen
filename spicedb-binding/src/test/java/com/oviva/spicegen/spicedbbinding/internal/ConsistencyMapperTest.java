package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.oviva.spicegen.api.Consistency;
import org.junit.jupiter.api.Test;

class ConsistencyMapperTest {

  @Test
  void map_consistent() {

    var consistency = Consistency.fullyConsistent();

    var sut = new ConsistencyMapper();

    // when
    var got = sut.map(consistency);

    // then
    assertEquals(
        com.authzed.api.v1.Consistency.RequirementCase.FULLY_CONSISTENT, got.getRequirementCase());
  }

  @Test
  void map_atLeastAsFresh() {

    var token = "maTokan";
    var consistency = Consistency.atLeastAsFreshAs(token);

    var sut = new ConsistencyMapper();

    // when
    var got = sut.map(consistency);

    // then
    assertEquals(
        com.authzed.api.v1.Consistency.RequirementCase.AT_LEAST_AS_FRESH, got.getRequirementCase());
    assertEquals(token, got.getAtLeastAsFresh().getToken());
  }
}

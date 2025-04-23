package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.ZedToken;
import com.oviva.spicegen.api.Consistency;

public class ConsistencyMapper {

  public com.authzed.api.v1.Consistency map(Consistency consistency) {
    return switch (consistency.requirement()) {
      case FULLY_CONSISTENT ->
          com.authzed.api.v1.Consistency.newBuilder().setFullyConsistent(true).build();
      case AT_LEAST_AS_FRESH ->
          com.authzed.api.v1.Consistency.newBuilder()
              .setAtLeastAsFresh(
                  ZedToken.newBuilder().setToken(consistency.consistencyToken()).build())
              .build();
        case MINIMIZE_LATENCY ->
            com.authzed.api.v1.Consistency.newBuilder()
                .setMinimizeLatency(true)
                .build();
    };
  }
}

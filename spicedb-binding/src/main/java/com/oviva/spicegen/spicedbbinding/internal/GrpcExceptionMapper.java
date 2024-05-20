package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.ClientException;
import io.grpc.StatusRuntimeException;

public class GrpcExceptionMapper {
  public ClientException map(StatusRuntimeException e) {
    return switch (e.getStatus().getCode()) {
      case PERMISSION_DENIED -> new ClientException("permission denied", e);
      case UNAUTHENTICATED -> new ClientException("unauthenticated", e);
      case ALREADY_EXISTS -> new ClientException("already exists", e);
      case INVALID_ARGUMENT -> new ClientException("invalid argument", e);
      case FAILED_PRECONDITION -> new ClientException("failed precondition", e);
      default -> new ClientException("unexpected status: " + e.getStatus(), e);
    };
  }
}

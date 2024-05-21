package com.oviva.spicegen.spicedbbinding.internal;

import com.oviva.spicegen.api.exceptions.*;
import io.grpc.StatusRuntimeException;

public class GrpcExceptionMapper {
  public ClientException map(StatusRuntimeException e) {
    return switch (e.getStatus().getCode()) {
      case PERMISSION_DENIED -> new AuthorizationException("permission denied", e);
      case UNAUTHENTICATED -> new AuthenticationException("unauthenticated", e);
      case ALREADY_EXISTS -> new ConflictException("already exists", e);
      case INVALID_ARGUMENT -> new ValidationException("invalid argument", e);
      case FAILED_PRECONDITION -> new ValidationException("failed precondition", e);
      default -> new ClientException("unexpected status: " + e.getStatus(), e);
    };
  }
}

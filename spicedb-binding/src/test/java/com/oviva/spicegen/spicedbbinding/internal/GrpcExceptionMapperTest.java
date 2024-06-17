package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.*;

import com.oviva.spicegen.api.exceptions.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

class GrpcExceptionMapperTest {

  private final GrpcExceptionMapper grpcExceptionMapper = new GrpcExceptionMapper();

  @Test
  void test_map_permissionDenied() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.PERMISSION_DENIED));

    assertInstanceOf(AuthorizationException.class, exception);
    assertEquals("permission denied", exception.getMessage());
  }

  @Test
  void test_map_unauthenticated() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.UNAUTHENTICATED));

    assertInstanceOf(AuthenticationException.class, exception);
    assertEquals("unauthenticated", exception.getMessage());
  }

  @Test
  void test_map_alreadyExists() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.ALREADY_EXISTS));

    assertInstanceOf(ConflictException.class, exception);
    assertEquals("already exists", exception.getMessage());
  }

  @Test
  void test_map_invalidArgument() {

    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    assertInstanceOf(ValidationException.class, exception);
    assertEquals("invalid argument", exception.getMessage());
  }

  @Test
  void test_map_failedPrecondition() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.FAILED_PRECONDITION));

    assertInstanceOf(ValidationException.class, exception);
    assertEquals("failed precondition", exception.getMessage());
  }

  @Test
  void test_map_unexpectedValue() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.CANCELLED));

    assertInstanceOf(ClientException.class, exception);
    assertTrue(exception.getMessage().startsWith("unexpected status:"));
  }
}

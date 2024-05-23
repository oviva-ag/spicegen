package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oviva.spicegen.api.exceptions.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

public class GrpcExceptionMapperTest {

  private final GrpcExceptionMapper grpcExceptionMapper = new GrpcExceptionMapper();

  @Test
  public void test_map_permissionDenied() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.PERMISSION_DENIED));

    assertTrue(exception instanceof AuthorizationException);
    assertEquals(exception.getMessage(), "permission denied");
  }

  @Test
  public void test_map_unauthenticated() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.UNAUTHENTICATED));

    assertTrue(exception instanceof AuthenticationException);
    assertEquals(exception.getMessage(), "unauthenticated");
  }

  @Test
  public void test_map_alreadyExists() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.ALREADY_EXISTS));

    assertTrue(exception instanceof ConflictException);
    assertEquals(exception.getMessage(), "already exists");
  }

  @Test
  public void test_map_invalidArgument() {

    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    assertTrue(exception instanceof ValidationException);
    assertEquals(exception.getMessage(), "invalid argument");
  }

  @Test
  public void test_map_failedPrecondition() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.FAILED_PRECONDITION));

    assertTrue(exception instanceof ValidationException);
    assertEquals(exception.getMessage(), "failed precondition");
  }

  @Test
  public void test_map_unexpectedValue() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.CANCELLED));

    assertTrue(exception instanceof ClientException);
    assertTrue(exception.getMessage().startsWith("unexpected status:"));
  }
}

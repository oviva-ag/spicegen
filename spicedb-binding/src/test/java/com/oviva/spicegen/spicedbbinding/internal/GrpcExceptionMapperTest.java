package com.oviva.spicegen.spicedbbinding.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.oviva.spicegen.api.exceptions.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

public class GrpcExceptionMapperTest {

  private final GrpcExceptionMapper grpcExceptionMapper = new GrpcExceptionMapper();

  @Test
  public void test_map_permissionDenied() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.PERMISSION_DENIED));

    assertThat(exception, instanceOf(AuthorizationException.class));
    assertThat(exception.getMessage(), equalTo("permission denied"));
  }

  @Test
  public void test_map_unauthenticated() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.UNAUTHENTICATED));

    assertThat(exception, instanceOf(AuthenticationException.class));
    assertThat(exception.getMessage(), equalTo("unauthenticated"));
  }

  @Test
  public void test_map_alreadyExists() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.ALREADY_EXISTS));

    assertThat(exception, instanceOf(ConflictException.class));
    assertThat(exception.getMessage(), equalTo("already exists"));
  }

  @Test
  public void test_map_invalidArgument() {

    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    assertThat(exception, instanceOf(ValidationException.class));
    assertThat(exception.getMessage(), equalTo("invalid argument"));
  }

  @Test
  public void test_map_failedPrecondition() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.FAILED_PRECONDITION));

    assertThat(exception, instanceOf(ValidationException.class));
    assertThat(exception.getMessage(), equalTo("failed precondition"));
  }

  @Test
  public void test_map_unexpectedValue() {
    var exception = grpcExceptionMapper.map(new StatusRuntimeException(Status.CANCELLED));

    assertThat(exception, instanceOf(ClientException.class));
    assertThat(exception.getMessage(), containsString("unexpected status:"));
  }
}

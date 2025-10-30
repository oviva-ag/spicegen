package com.oviva.spicegen.spicedbbinding;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.oviva.spicegen.api.PermissionService;
import com.oviva.spicegen.spicedbbinding.internal.SpiceDbPermissionServiceImpl;
import java.time.Duration;

public class SpiceDbPermissionServiceBuilder {
  private PermissionsServiceGrpc.PermissionsServiceBlockingStub stub;
  private Duration requestTimeout = Duration.ofSeconds(3);

  public static SpiceDbPermissionServiceBuilder newBuilder() {
    return new SpiceDbPermissionServiceBuilder();
  }

  private SpiceDbPermissionServiceBuilder() {}

  public SpiceDbPermissionServiceBuilder permissionsBlockingStub(
      PermissionsServiceGrpc.PermissionsServiceBlockingStub stub) {
    this.stub = stub;
    return this;
  }

  public SpiceDbPermissionServiceBuilder requestTimeout(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
    return this;
  }

  public PermissionService build() {
    return new SpiceDbPermissionServiceImpl(stub, requestTimeout);
  }
}

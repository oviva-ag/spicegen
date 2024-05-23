package com.oviva.spicegen.spicedbbinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.grpcutil.BearerToken;
import com.oviva.spicegen.api.PermissionService;
import io.grpc.ManagedChannelBuilder;
import org.junit.Test;

public class SpiceDbPermissionServiceBuilderTest {

  @Test
  public void test_spiceDbPermissionServiceBuilder() {

    var token = "t0ken";
    var host = "127.0.0.1";
    var port = 50051;

    var bearerToken = new BearerToken(token);
    var channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    var permissionsService =
        PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

    var svc =
        SpiceDbPermissionServiceBuilder.newBuilder()
            .permissionsBlockingStub(permissionsService)
            .build();

    assertThat(svc, instanceOf(PermissionService.class));
  }
}

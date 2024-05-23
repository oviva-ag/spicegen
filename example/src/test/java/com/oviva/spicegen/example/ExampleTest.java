package com.oviva.spicegen.example;

import static com.authzed.api.v1.PermissionService.CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.authzed.api.v1.Core;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.api.v1.SchemaServiceOuterClass;
import com.authzed.grpcutil.BearerToken;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.PermissionService;
import com.oviva.spicegen.api.SubjectRef;
import com.oviva.spicegen.api.UpdateRelationships;
import com.oviva.spicegen.permissions.SchemaConstants;
import com.oviva.spicegen.permissions.refs.DocumentRef;
import com.oviva.spicegen.permissions.refs.FolderRef;
import com.oviva.spicegen.permissions.refs.UserRef;
import com.oviva.spicegen.spicedbbinding.SpiceDbPermissionServiceBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ExampleTest {

  private static final int GRPC_PORT = 50051;
  private static final String TOKEN = "t0ken";

  private static final Logger logger = LoggerFactory.getLogger(ExampleTest.class);

  @Container
  private GenericContainer<?> spicedb =
      new GenericContainer<>(DockerImageName.parse("quay.io/authzed/spicedb:v1.32.0"))
          .withCommand("serve", "--grpc-preshared-key", TOKEN)
          .waitingFor(new LogMessageWaitStrategy().withRegEx(".*\"grpc server started serving\".*"))
          .withLogConsumer(f -> logger.info("spicedb: {}", f.getUtf8String()))
          .withExposedPorts(
              GRPC_PORT, // grpc
              8080, // dashboard
              9090 // metrics
              );

  private PermissionService permissionService;
  private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionServiceStub;
  private ManagedChannel channel;

  @BeforeEach
  void before() {

    var host = spicedb.getHost();
    var port = spicedb.getMappedPort(GRPC_PORT);

    var bearerToken = new BearerToken(TOKEN);
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    updateSchema();

    // setup the GRPC stub
    permissionServiceStub =
        PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

    // create the permissions service
    permissionService =
        SpiceDbPermissionServiceBuilder.newBuilder()
            .permissionsBlockingStub(permissionServiceStub)
            .build();
  }

  private void updateSchema() {
    var schemaService =
        SchemaServiceGrpc.newBlockingStub(channel).withCallCredentials(new BearerToken(TOKEN));

    schemaService.writeSchema(
        SchemaServiceOuterClass.WriteSchemaRequest.newBuilder().setSchema(loadSchema()).build());
  }

  @Test
  void example() {
    var userId = 7;

    // typesafe object references!
    var user = UserRef.ofLong(userId);
    var folder = FolderRef.of("home");
    var document = DocumentRef.ofLong(48);

    // EXAMPLE: updating relationships
    var updateResult =
        permissionService.updateRelationships(
            UpdateRelationships.newBuilder()
                // note the generated factory methods!
                .update(folder.createReaderUser(user))
                .update(document.createParentFolderFolder(folder))
                .build());

    var consistencyToken = updateResult.consistencyToken();

    // EXAMPLE: checking permission
    var res =
        checkPermission(
            document,
            // note the generated constants!
            SchemaConstants.PERMISSION_DOCUMENT_READ,
            SubjectRef.ofObject(user),
            consistencyToken);

    assertEquals(PERMISSIONSHIP_HAS_PERMISSION, res.getPermissionship());
  }

  private com.authzed.api.v1.PermissionService.CheckPermissionResponse checkPermission(
      ObjectRef object, String permission, SubjectRef subject, String consistencyToken) {

    return permissionServiceStub.checkPermission(
        com.authzed.api.v1.PermissionService.CheckPermissionRequest.newBuilder()
            .setPermission(permission)
            .setResource(
                Core.ObjectReference.newBuilder()
                    .setObjectType(object.kind())
                    .setObjectId(object.id())
                    .build())
            .setSubject(
                Core.SubjectReference.newBuilder()
                    .setObject(
                        Core.ObjectReference.newBuilder()
                            .setObjectType(subject.kind())
                            .setObjectId(subject.id())
                            .build())
                    .build())
            .setConsistency(
                com.authzed.api.v1.PermissionService.Consistency.newBuilder()
                    .setAtLeastAsFresh(
                        Core.ZedToken.newBuilder().setToken(consistencyToken).build())
                    .build())
            .build());
  }

  private String loadSchema() {
    try (var is = this.getClass().getResourceAsStream("/files.zed")) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      fail(e);
    }
    return "";
  }
}

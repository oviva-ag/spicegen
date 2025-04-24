package com.oviva.spicegen.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.api.v1.WriteSchemaRequest;
import com.authzed.grpcutil.BearerToken;
import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.PermissionService;
import com.oviva.spicegen.api.SubjectRef;
import com.oviva.spicegen.api.UpdateRelationships;
import com.oviva.spicegen.permissions.refs.DocumentRef;
import com.oviva.spicegen.permissions.refs.FolderRef;
import com.oviva.spicegen.permissions.refs.TeamRef;
import com.oviva.spicegen.permissions.refs.UserRef;
import com.oviva.spicegen.spicedbbinding.SpiceDbPermissionServiceBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class ExampleTest {

  private static final int GRPC_PORT = 50051;
  private static final String TOKEN = "t0ken";

  private static final Logger logger = LoggerFactory.getLogger(ExampleTest.class);

  @Container
  private GenericContainer<?> spicedb =
      new GenericContainer<>(DockerImageName.parse("authzed/spicedb:v1.41.0"))
          .withCommand("serve", "--grpc-preshared-key", TOKEN)
          .waitingFor(Wait.forLogMessage(".*\"grpc server started serving\".*", 1))
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

    schemaService.writeSchema(WriteSchemaRequest.newBuilder().setSchema(loadSchema()).build());
  }

  @Test
  void example() {
    var userId = 7;

    // typesafe object references!
    var user = UserRef.ofLong(userId);
    var userInTeam = UserRef.ofLong(42);
    var team = TeamRef.ofLong(42);
    var folder = FolderRef.of("home");
    var document = DocumentRef.ofLong(48);

    // EXAMPLE: updating relationships
    var updateResult =
        permissionService.updateRelationships(
            UpdateRelationships.newBuilder()
                // note the generated factory methods!
                .update(folder.createReaderUser(user))
                .update(team.createMemberUser(userInTeam))
                .update(folder.createReaderTeamMember(team))
                .update(document.createParentFolderFolder(folder))
                .build());

    var consistencyToken = updateResult.consistencyToken();

    // EXAMPLE: checking permission
    assertTrue(
        permissionService.checkPermission(
            document.checkRead(
                SubjectRef.ofObject(user), Consistency.atLeastAsFreshAs(consistencyToken))));

    assertTrue(
        permissionService.checkPermission(
            document.checkRead(
                SubjectRef.ofObject(userInTeam), Consistency.atLeastAsFreshAs(consistencyToken))));

    // EXAMPLE: checking multiple permissions
    var checkPermissions =
        permissionService.checkBulkPermissions(
            CheckBulkPermissions.newBuilder()
                .item(document.checkBulkRead(SubjectRef.ofObject(user)))
                .item(folder.checkBulkRead(SubjectRef.ofObject(UserRef.of("non-existing"))))
                .consistency(Consistency.atLeastAsFreshAs(consistencyToken))
                .build());

    assertEquals(2, checkPermissions.size());
    assertTrue(checkPermissions.get(0).permissionGranted());
    assertFalse(checkPermissions.get(1).permissionGranted());
    Iterator<UserRef> usersAllowedToRead =
        permissionService.lookupSubjects(document.lookupReadUser());
    assertTrue(usersAllowedToRead.hasNext());
    // usersAllowedToRead contains both userId and user2
    var userIds =
        StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(usersAllowedToRead, Spliterator.ORDERED), false)
            .map(UserRef::id)
            .toList();
    assertEquals(2, userIds.size());
    assertTrue(userIds.contains(user.id()));
    assertTrue(userIds.contains(user2.id()));
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

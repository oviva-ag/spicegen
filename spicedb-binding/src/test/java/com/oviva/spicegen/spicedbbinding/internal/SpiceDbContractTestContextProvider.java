package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.grpcutil.BearerToken;
import com.oviva.spicegen.spicedbbinding.test.GenericTypedParameterResolver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.*;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SpiceDbContractTestContextProvider implements TestTemplateInvocationContextProvider {

  private static final String TOKEN = "t0ken";
  private static final int GRPC_PORT = 50051;

  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext extensionContext) {

    return Stream.of(inMemorySpiceDB(), postgresSpiceDB());
  }

  private TestTemplateInvocationContext inMemorySpiceDB() {

    var spicedb =
        new GenericContainer<>(DockerImageName.parse("quay.io/authzed/spicedb:v1.32.0"))
            .withCommand("serve", "--grpc-preshared-key", TOKEN)
            .withExposedPorts(
                GRPC_PORT, // grpc
                8080, // dashboard
                9090 // metrics
                );

    spicedb.start();

    var host = spicedb.getHost();
    var port = spicedb.getMappedPort(GRPC_PORT);

    var services = createServices(host, port);

    return createContext(
        "in-memory SpiceDB",
        services,
        () -> {
          quitelyShutdown(services.channel());
          spicedb.stop();
        });
  }

  private TestTemplateInvocationContext postgresSpiceDB() {

    var spiceDbServiceName = "spicedb_1";
    var environment =
        new DockerComposeContainer<>(new File("src/main/docker/compose.dev.yaml"))
            .withExposedService(spiceDbServiceName, GRPC_PORT);

    environment.start();

    var host = environment.getServiceHost(spiceDbServiceName, GRPC_PORT);
    var port = environment.getServicePort(spiceDbServiceName, GRPC_PORT);
    var services = createServices(host, port);
    return createContext(
        "postgres backed SpiceDB",
        services,
        () -> {
          quitelyShutdown(services.channel());
          environment.stop();
        });
  }

  private void quitelyShutdown(ManagedChannel channel) {
    channel.shutdownNow();
    try {
      channel.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // quietly ignore
    }
  }

  public TestTemplateInvocationContext createContext(
      String displayName, TestServices services, Runnable done) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return displayName;
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return List.of(
            new GenericTypedParameterResolver<>(services.permissionsService()),
            new GenericTypedParameterResolver<>(services.schemaService()),
            new GenericTypedParameterResolver<>(services.channel()),
            (AfterAllCallback)
                extensionContext -> {
                  if (done != null) {
                    done.run();
                  }
                });
      }
    };
  }

  public TestServices createServices(String host, int port) {

    var channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    var bearerToken = new BearerToken(TOKEN);

    var schemaService = SchemaServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

    var permissionsService =
        PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);
    return new TestServices(channel, schemaService, permissionsService);
  }

  public static final class TestServices {

    private final ManagedChannel channel;
    private final SchemaServiceGrpc.SchemaServiceBlockingStub schemaService;
    private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

    public TestServices(
        ManagedChannel channel,
        SchemaServiceGrpc.SchemaServiceBlockingStub schemaService,
        PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService) {
      this.channel = channel;
      this.schemaService = schemaService;
      this.permissionsService = permissionsService;
    }

    public ManagedChannel channel() {
      return channel;
    }

    public SchemaServiceGrpc.SchemaServiceBlockingStub schemaService() {
      return schemaService;
    }

    public PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService() {
      return permissionsService;
    }
  }
}

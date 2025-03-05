package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.grpcutil.BearerToken;
import com.oviva.spicegen.spicedbbinding.test.GenericTypedParameterResolver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class SpiceDbContractTestContextProvider implements TestTemplateInvocationContextProvider {

  private static Logger logger = LoggerFactory.getLogger(SpiceDbContractTestContextProvider.class);
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
        createSpicedbBaseContainer("serve", "--grpc-preshared-key", TOKEN)
            .withExposedPorts(GRPC_PORT)
            .waitingFor(
                new LogMessageWaitStrategy().withRegEx(".*\"grpc server started serving\".*"));
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

    var net = Network.newNetwork();

    var migrator =
        createSpicedbBaseContainer(
                "migrate",
                "head",
                "--datastore-engine",
                "postgres",
                "--datastore-conn-uri",
                "postgres://spicedb-pg:5432/spicedb?sslmode=disable&user=postgres&password=root")
            .withStartupCheckStrategy(
                new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(5)))
            .withNetwork(net);

    var spicedb = createPostgeresSpicedbContainer(net);

    var db =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("14"))
            .withDatabaseName("spicedb")
            .withPassword("root")
            .withUsername("postgres")
            .withNetworkAliases("spicedb-pg")
            .withNetwork(net);

    db.start();
    migrator.start();
    spicedb.start();

    var host = spicedb.getHost();
    var port = spicedb.getMappedPort(GRPC_PORT);
    var services = createServices(host, port);
    return createContext(
        "postgres backed SpiceDB",
        services,
        () -> {
          quitelyShutdown(services.channel());
          migrator.stop();
          spicedb.stop();
          db.stop();
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

  private GenericContainer<?> createPostgeresSpicedbContainer(Network net) {

    return createSpicedbBaseContainer(
            "serve",
            "--grpc-preshared-key=%s".formatted(TOKEN),
            "--datastore-engine=postgres",
            "--datastore-conn-uri=postgres://spicedb-pg:5432/spicedb?sslmode=disable&user=postgres&password=root")
        .waitingFor(Wait.forLogMessage(".*\"grpc server started serving\".*", 1))
        .withExposedPorts(
            GRPC_PORT, // grpc
            8080, // dashboard
            9090 // metrics
            )
        .withNetwork(net);
  }

  private GenericContainer<?> createSpicedbBaseContainer(String... args) {

    return new GenericContainer<>(DockerImageName.parse("authzed/spicedb:v1.41.0"))
        .withCommand(args)
        .withLogConsumer(f -> logger.info("spicedb {}: {}", args[0], f.getUtf8String()));
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

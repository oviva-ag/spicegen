package com.oviva.spicegen.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstPreProcessor {

  private static Logger logger = LoggerFactory.getLogger(AstPreProcessor.class);

  public void parse(Path astDestination, Path schemaSource) {

    try {
      var binary = "spicegen_parser";
      var parser =
          Files.createTempFile(
              binary,
              null,
              PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));

      loadParserBinary(parser);

      var builder =
          new ProcessBuilder(
                  parser.toString(),
                  "-schema-path",
                  schemaSource.toString(),
                  "-out-path",
                  astDestination.toString())
              .redirectError(ProcessBuilder.Redirect.INHERIT);

      var args =
          builder.command().stream().map(s -> "'" + s + "'").collect(Collectors.joining(" "));
      logger.info("executing {}", args);

      var process = builder.start();

      var success = process.waitFor(20, TimeUnit.SECONDS);
      if (!success) {
        throw new IllegalStateException(
            "%s did not terminate within its timeout".formatted(binary));
      }

      var exit = process.exitValue();
      if (exit != 0) {
        throw new IllegalStateException(
            "%s terminated with non-zero status %d".formatted(binary, exit));
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void loadParserBinary(Path parserDestination) {

    var target = determineTarget();

    var binary = "spicegen_%s".formatted(target);

    logger.info("moving pre-processor '{}' from to '{}'", binary, parserDestination);

    try (var is = this.getClass().getResourceAsStream("/" + binary);
        var os =
            Files.newOutputStream(
                parserDestination,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE); ) {
      is.transferTo(os);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String determineTarget() {

    var target = "%s_%s".formatted(determineOs(), determineArch());
    return target;
  }

  private String determineArch() {
    var raw = System.getProperty("os.arch");

    return switch (raw) {
      case "aarch64" -> "arm64";
      case "i386" -> "386";
      case "amd64" -> "amd64_v1";
      default ->
          throw new UnsupportedOperationException("architecture '%s' not supported".formatted(raw));
    };
  }

  private String determineOs() {
    var raw = System.getProperty("os.name");

    return switch (raw) {
      case "Mac OS X" -> "darwin";
      case "Linux" -> "linux";
      default -> throw new UnsupportedOperationException("OS '%s' not supported".formatted(raw));
    };
  }
}

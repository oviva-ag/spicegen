# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Project Does

SpiceGen generates type-safe Java code from SpiceDB `.zed` schema files. It produces:
- Type-safe object references (`*Ref` classes) with factory methods
- Relationship update builders (`folder.createReaderUser(user)`)
- Permission check helpers (`document.checkRead(subject, consistency)`)
- Schema constants for namespaces, permissions, and relations

## Build Commands

```bash
# Build all Maven modules
mvn install

# Build without tests
mvn install -DskipTests

# Run a single test class
mvn test -pl generator -Dtest=SpiceDbClientGeneratorTest

# Run integration tests (requires Docker)
mvn verify -Pintegration-tests

# Build Gradle plugin (separate build system)
cd generator-gradle-plugin && ./gradlew build

# Test Gradle example
cd gradle-example && ./gradlew generateSpiceDbSources test
```

## Architecture

The code generation pipeline:
```
schema.zed → Go parser (AST JSON) → Java model → JavaPoet → Generated sources
```

**Module responsibilities:**
- `parser/` - Go binary that uses SpiceDB's official parser to produce JSON AST. Built with GoReleaser, binaries bundled in `model/src/main/resources/`
- `model/` - Java data model (Schema, ObjectDefinition, Relation, Permission) and AST-to-model mapping
- `generator/` - JavaPoet-based code generator producing `*Ref` classes and `SchemaConstants`
- `api/` - Runtime interfaces (PermissionService, ObjectRef, UpdateRelationship)
- `spicedb-binding/` - gRPC implementation bridging `api` to SpiceDB
- `generator-maven-plugin/` - Maven plugin wrapping the generator
- `generator-gradle-plugin/` - Gradle plugin (standalone Gradle project, not in Maven reactor)

## Rebuilding the Go Parser

When upgrading SpiceDB version in `parser/go.mod`:
```bash
cd parser
go mod tidy
make build  # Requires goreleaser installed
```
This rebuilds native binaries for all platforms and copies them to `model/src/main/resources/`.

## Testing

- Unit tests: standard JUnit 5 in each module
- Integration tests: `example/` module uses TestContainers to spin up real SpiceDB instances
- Gradle plugin tests: `generator-gradle-plugin/src/functionalTest/`

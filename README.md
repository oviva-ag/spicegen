![Maven Central Version](https://img.shields.io/maven-central/v/com.oviva.spicegen/api)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oviva-ag_spicegen&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oviva-ag_spicegen)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oviva-ag_spicegen&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oviva-ag_spicegen)
# SpiceDB Client Generator

- Bored of copy & pasting strings from your SpiceDB schema into your client code?
- Had enough bugs or downtimes due to client library bugs due to typing and typo mistakes?

*Look no further!*

This is a Java generator for SpiceDB schemas which generates:

- **constants** - generates `string` constants for object definitions, permissions and relations
- **type-safe references** - generates type-safe object references
- **type-safe relationship updates** - provides factory methods to assemble type-safe relationship updates

## Getting Started

**Prerequisites:**

- [GitHub packages for Maven](https://docs.github.com/de/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

### Maven

1. Add the `com.oviva.spicegen:api` dependency
2. Add the `com.oviva.spicegen:spicegen-maven-plugin` plugin

Example `pom.xml`

```xml
<!-- ... -->
<dependencies>
    <dependency>
        <groupId>com.oviva.spicegen</groupId>
        <artifactId>api</artifactId>
        <version>${spicegen.version}</version>
    </dependency>
</dependencies>
<!-- ... -->
<plugins>
<plugin>
    <groupId>com.oviva.spicegen</groupId>
    <artifactId>spicegen-maven-plugin</artifactId>
    <version>${spicegen.version}</version>
    <executions>
        <execution>
            <configuration>
                <schemaPath>${project.basedir}/src/main/resources/schema.zed</schemaPath>
                <packageName>${project.groupId}.permissions</packageName>
                <outputDirectory>${project.basedir}/target/generated-sources/src/main/java</outputDirectory>
            </configuration>
            <goals>
                <goal>spicegen</goal>
            </goals>
        </execution>
    </executions>
</plugin>
</plugins>
```

### Gradle

1. Add the `com.oviva.spicegen:api` dependency
2. Apply the `com.oviva.spicegen` plugin

Example `build.gradle.kts`

```kotlin
plugins {
    java
    id("com.oviva.spicegen") version "$spicegenVersion"
}

dependencies {
    implementation("com.oviva.spicegen:api:$spicegenVersion")
}

// Optional: customize the defaults
spicegen {
    schemaFile.set(file("src/main/resources/schema.zed"))
    packageName.set("com.example.permissions")
    outputDirectory.set(layout.buildDirectory.dir("generated/sources/spicegen/java/main"))
}
```

The `generateSpiceDbSources` task runs automatically before `compileJava`.

## Implementation Overview

```mermaid
graph LR
    schema[/schema.zed/] -- " pre-process to AST (go) " --> ast[Abstract Syntax Tree]
    ast -- " read and map (java) " --> model[Schema Model]
    model -- generate --> source[/TypeDefs & Schema Constants/]
```

The generator work in multiple stages that could be re-used for other generators, namely:

1. The SpiceDB schema is parsed (`*.zed`) into an AST by the official lexer and parser. See [parser](./parser).
2. The AST is serialized to JSON, which in turn is picked up by the Java generator and transformed
   into a nice model. See [model](./model).
3. The schema model is transformed into Java sources. See [generator](./generator)

To make this easy to use, all the above is bundled in the [Maven plugin](./generator-maven-plugin) and [Gradle plugin](./generator-gradle-plugin).

## Development

### Building

```bash
# Build Maven modules
mvn install

# Build and test Gradle plugin
cd generator-gradle-plugin
./gradlew build
```

### IDE Setup (IntelliJ IDEA)

The project is primarily a Maven multi-module project. The Gradle plugin (`generator-gradle-plugin`) is a standalone Gradle project because Gradle plugins require Gradle's toolchain to build.

To work with the Gradle plugin in IntelliJ:

1. Open the project as a Maven project (the root `pom.xml`)
2. Right-click on `generator-gradle-plugin/build.gradle.kts`
3. Select **"Link Gradle Project"**

IntelliJ will then recognize both the Maven modules and the Gradle plugin.

## Useful Links

- [SpiceDB API Docs](https://buf.build/authzed/api/docs/main/authzed.api.v1)

## Wishlist

- type-safe IDs, needs additional metadata in the schema
- generate caveats
- permission check boilerplate, might need additional schema metadata

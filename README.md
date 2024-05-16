# SpiceDB Client Generator

- Bored of copy & pasting strings from your SpiceDB schema into your client code?
- Had enough bugs or downtimes due to client library bugs due to typing and typo mistakes?

*Look no further!*

This is a Java generator for SpiceDB schemas which:

- generates `String` constants for object definitions, permissions and relations
- generates type-safe object references
- provides factory methods to assemble type-safe relationship updates

## Getting Started

1. Add the  `com.oviva.spicegen:api` dependency
2. Add the  `com.oviva.spicegen:spicegen-maven-plugin` plugin

Example `pom.xml`

```xml
...
<dependencies>
    <dependency>
        <groupId>com.oviva.spicegen</groupId>
        <artifactId>api</artifactId>
        <version>${spicegen.version}</version>
    </dependency>
</dependencies>
        ...
<plugins>
<plugin>
    <groupId>com.oviva.spicegen</groupId>
    <artifactId>spicegen-maven-plugin</artifactId>
    <version>${spicegen.version}</version>
    <executions>
        <execution>
            <configuration>
                <!-- NOTE: for now the schema needs to be pre-processed into an AST -->
                <asyncApiPath>${project.basedir}/src/main/resources/schema_ast.json</asyncApiPath>
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

# SpiceDB Client Generator

This is a Java generator for SpiceDB schemas. It generates the constants and typesafe object references
for a given SpiceDB schema.

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

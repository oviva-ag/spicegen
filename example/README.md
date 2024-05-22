# Spicegen Example

## Code
See [ExampleTest](./src/test/java/com/oviva/spicegen/example/ExampleTest.java).


## Maven Setup
Example [pom.xml](./pom.xml)
```xml
<project>

    <!-- ... -->

    <!--    see https://docs.github.com/de/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registr&lt;!&ndash;&ndash;&gt;y-->
    <repositories>
        <repository>
            <id>spicegen</id>
            <name>GitHub Oviva Spicegen</name>
            <url>https://maven.pkg.github.com/oviva-ag/spicegen</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>spicegen</id>
            <name>GitHub Oviva Spicegen Plugin</name>
            <url>https://maven.pkg.github.com/oviva-ag/spicegen</url>
        </pluginRepository>
    </pluginRepositories>

    <!-- ... -->

    <dependencies>
        <dependency>
            <groupId>com.oviva.spicegen</groupId>
            <artifactId>api</artifactId>
            <version>...</version>
        </dependency>
        <dependency>
            <groupId>com.oviva.spicegen</groupId>
            <artifactId>spicedb-binding</artifactId>
            <version>...</version>
        </dependency>
    </dependencies>

    <!-- ... -->

    <build>
        <plugins>
            <plugin>
                <groupId>com.oviva.spicegen</groupId>
                <artifactId>spicegen-maven-plugin</artifactId>
                <version>${project.version}</version>
                <executions>
                    <execution>
                        <configuration>
                            <schemaPath>${project.basedir}/src/test/resources/files.zed</schemaPath>
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
    </build>
</project>
```
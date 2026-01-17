plugins {
    `java-gradle-plugin`
    id("com.vanniktech.maven.publish") version "0.30.0"
}

// Dynamic version from CI
val releaseVersion: String = System.getenv("VERSION") ?: "1.0.0-SNAPSHOT"

group = "com.oviva.spicegen"
version = releaseVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenLocal() // Must be first for CI to find freshly-built Maven artifacts
    mavenCentral()
}

dependencies {
    implementation("com.oviva.spicegen:model:$releaseVersion")
    implementation("com.oviva.spicegen:generator:$releaseVersion")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

gradlePlugin {
    website.set("https://github.com/oviva-ag/spicegen")
    vcsUrl.set("https://github.com/oviva-ag/spicegen")
    plugins {
        create("spicegen") {
            id = "com.oviva.spicegen"
            displayName = "SpiceDB Code Generator"
            description = "Generates type-safe Java clients for SpiceDB from .zed schema files"
            implementationClass = "com.oviva.spicegen.gradle.SpicegenPlugin"
            tags.set(listOf("spicedb", "code-generation", "authorization"))
        }
    }
}

// Functional test configuration
val functionalTestSourceSet: SourceSet = sourceSets.create("functionalTest")

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.check {
    dependsOn(functionalTest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.oviva.spicegen", "spicegen-gradle-plugin", releaseVersion)

    pom {
        name.set("SpiceGen Gradle Plugin")
        description.set("Gradle plugin for generating type-safe Java clients for SpiceDB")
        url.set("https://github.com/oviva-ag/spicegen")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("trichner")
                name.set("Thomas Richner")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/oviva-ag/spicegen.git")
            url.set("https://github.com/oviva-ag/spicegen")
        }
    }
}

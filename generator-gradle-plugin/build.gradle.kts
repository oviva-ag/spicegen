plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.oviva.spicegen"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.oviva.spicegen:model:1.0.0-SNAPSHOT")
    implementation("com.oviva.spicegen:generator:1.0.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
}

gradlePlugin {
    plugins {
        create("spicegen") {
            id = "com.oviva.spicegen"
            displayName = "SpiceDB Code Generator"
            description = "Generates type-safe Java clients for SpiceDB from .zed schema files"
            implementationClass = "com.oviva.spicegen.gradle.SpicegenPlugin"
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

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/oviva-ag/spicegen")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

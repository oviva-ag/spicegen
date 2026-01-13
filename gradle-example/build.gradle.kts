plugins {
    java
    id("com.oviva.spicegen")
}

group = "com.example"
version = "1.0.0"

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
    implementation("com.oviva.spicegen:api:1.0.0-SNAPSHOT")
}

spicegen {
    schemaFile.set(file("src/main/resources/schema.zed"))
    packageName.set("com.example.permissions")
}

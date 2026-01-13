pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "gradle-example"

// Include the plugin from the local project for testing
includeBuild("../generator-gradle-plugin")

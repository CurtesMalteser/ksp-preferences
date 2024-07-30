plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.google.protobuf)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.curtesmalteser.kspreferences"
            artifactId = "annotation"
            version = "0.0.1"

            from(components["java"])
        }
    }
}
import com.curtesmalteser.publish.PublishArtifactTask

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.google.protobuf)
    id("maven-publish")

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.register<PublishArtifactTask>("publishMyArtifact") {
    artifactId.set("annotation")
}
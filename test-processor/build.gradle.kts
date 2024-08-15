import com.curtesmalteser.publish.PublishArtifactTask

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.symbol.processing.api)
    implementation(project(":annotation"))
    implementation(project(":writer"))
}

tasks.register<PublishArtifactTask>("publishMyArtifact") {
    artifactId.set("test-processor")
}
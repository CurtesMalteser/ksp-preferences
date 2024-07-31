import com.curtesmalteser.publish.PublishArtifactTask

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.symbol.processing.api)
    implementation(project(":annotation"))
}

tasks.register<PublishArtifactTask>("publishMyArtifact") {
    artifactId.set("processor")
}
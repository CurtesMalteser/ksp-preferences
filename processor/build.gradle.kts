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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.curtesmalteser.kspreferences"
            artifactId = "processor"
            version = "0.0.1"

            from(components["java"])
        }
    }
}
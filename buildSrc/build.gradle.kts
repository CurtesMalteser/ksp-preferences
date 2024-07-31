plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("publishArtifactPlugin") {
            id = "com.curtesmalteser.publish"
            implementationClass = "com.curtesmalteser.publish.PublishArtifactTask"
        }
    }
}
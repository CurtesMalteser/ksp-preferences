package com.curtesmalteser.publish

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import java.io.FileInputStream
import java.util.Properties

/**
 * Created by António Bastião on 30.07.2024
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */

abstract class PublishArtifactTask : DefaultTask() {

    @get:Input
    abstract val artifactId: Property<String>

    @TaskAction
    fun publishArtifact() {

        val processorPropertiesFile = project.rootProject.file("processor.properties")
        val processorProperties = Properties()
        processorProperties.load(FileInputStream(processorPropertiesFile))

        project.extensions.configure<PublishingExtension>("publishing") {
            publications {
                create<MavenPublication>("maven") {
                    groupId = processorProperties["groupId"].toString()
                    artifactId = artifactId
                    version = processorProperties["version"].toString()

                    from(project.components.getByName("java"))
                }
            }
        }
    }
}
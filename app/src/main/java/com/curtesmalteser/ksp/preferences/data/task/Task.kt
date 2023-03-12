package com.curtesmalteser.ksp.preferences.data.task

/**
 * Added by António Bastião on 12.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 * Copy from <a href="https://raw.githubusercontent.com/googlecodelabs/android-datastore/proto_datastore/app/src/main/java/com/codelab/android/datastore/data/Task.kt">Android Codelab Android Datastore</a>
 */
import java.util.*

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

data class Task(
    val name: String,
    val deadline: Date,
    val priority: TaskPriority,
    val completed: Boolean = false
)
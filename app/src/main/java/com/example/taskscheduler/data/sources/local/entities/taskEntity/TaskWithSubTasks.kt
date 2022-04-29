package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.taskscheduler.data.models.TaskModel

data class TaskWithSubTasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "titleID",
        entityColumn = "superTask"
    )
    val subTaskEntities: List<SubTaskEntity>
) {
    fun hasSubTask(): Boolean = subTaskEntities.isNotEmpty()

    fun toModel(): TaskModel {
        return if (hasSubTask()) {
            TaskModel(task.title, task.type, task.description)
        } else {
            TODO("TaskDao needed to interact with the database.")
        }
    }
}
package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.taskscheduler.domain.models.TaskModel

data class TaskWithSuperTask(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = titleID,
        entityColumn = subTaskID
    )
    val superTaskEntity: SubTaskEntity?
) {
    fun toModel() = TaskModel (
        title = task.title, type = task.type, description = task.description, isDone = task.isDone,
        superTask = superTaskEntity?.superTask ?: "",
    )

}

fun Iterable<TaskWithSuperTask>.toModel(): List<TaskModel> = map(TaskWithSuperTask::toModel)

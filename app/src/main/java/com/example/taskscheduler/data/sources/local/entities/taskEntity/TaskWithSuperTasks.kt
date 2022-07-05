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
    fun toModel() = TaskModel (this)

}

fun Iterable<TaskWithSuperTask>.toModel(): List<TaskModel> = map(TaskWithSuperTask::toModel)

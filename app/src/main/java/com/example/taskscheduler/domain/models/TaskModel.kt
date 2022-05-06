package com.example.taskscheduler.domain.models

import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity

//TODO: implement TaskJson.

data class TaskModel (
    val title: String,
    var type: String,
    var description: String = "",
    var superTask: String = "",
    var subTasks: List<String> = emptyList(),
    var isDone: Boolean = false
) {

    val hasDescription get() = description.isNotBlank()
    val hasSuperTask get() = superTask.isNotBlank()
    val hasSubTasks get() = subTasks.isNotEmpty()
    val numSubTasks get() = subTasks.size

    fun toEntity() = TaskEntity(
        title = title, type = type, description = description, isDone = isDone
    )

    /**Returns a SubTaskEntity with the relationship of hierarchy whit its father, or null if does not have father.*/
    fun toSuperTaskEntity() = if (hasSuperTask) SubTaskEntity(superTask = superTask, subTask = title) else null

    /**Returns a List of SubTaskEntity with the relationships of hierarchy whit its children.*/
    fun toSubTasksEntities() = subTasks.map { subTask ->
        SubTaskEntity(superTask = title, subTask = subTask)
    }
}

fun Iterable<TaskModel>.toEntity(): List<TaskEntity> = map(TaskModel::toEntity)

fun Iterable<TaskModel>.toSuperTaskEntity(): List<SubTaskEntity> = mapNotNull(TaskModel::toSuperTaskEntity)

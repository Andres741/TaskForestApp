package com.example.taskscheduler.domain.models

import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperTask
import java.util.*

//TODO: implement TaskJson.

data class TaskModel (
    val title: String,
    val type: String,
    var description: String = "",
    val superTask: String = "",
    val subTasks: List<String> = emptyList(),
    var isDone: Boolean = false,
    val dateNum: Long = System.currentTimeMillis(),
): ITaskTypeNameOwner {

    val date: Calendar get() = dateNum.let { Calendar.getInstance().apply { timeInMillis = it } }
    val hasDescription get() = description.isNotBlank()
    val hasSuperTask get() = superTask.isNotBlank()
    val hasSubTasks get() = subTasks.isNotEmpty()
    val numSubTasks get() = subTasks.size
    override val typeName: String get() = type

    constructor(entity: TaskEntity): this (
        title = entity.title, type = entity.type, description = entity.description,
        isDone = entity.isDone, dateNum = entity.date
    )
    constructor(entity: TaskWithSuperTask): this (
        title = entity.task.title, type = entity.task.type, description = entity.task.description,
        isDone = entity.task.isDone, dateNum = entity.task.date,
        superTask = entity.superTaskEntity?.superTask ?: ""
    )

    constructor(entity: TaskWithSuperAndSubTasks): this (
        title = entity.task.title, type = entity.task.type, description = entity.task.description,
        isDone = entity.task.isDone, dateNum = entity.task.date,
        superTask = entity.superTaskEntity?.superTask ?: "",
        subTasks = entity.subTaskEntities.map(SubTaskEntity::subTask)
    )

    fun toEntity() = TaskEntity(
        title = title, type = type, description = description, isDone = isDone, date = dateNum
    )

    /**Returns a SubTaskEntity with the relationship of hierarchy whit its father, or null if does not have father.*/
    fun toSuperTaskEntity() = if (hasSuperTask) SubTaskEntity(superTask = superTask, subTask = title) else null

    /**Returns a List of SubTaskEntity with the relationships of hierarchy whit its children.*/
    fun toSubTasksEntities() = subTasks.map { subTask ->
        SubTaskEntity(superTask = title, subTask = subTask)
    }

    override fun toString() =
        "TaskModel(title=$title, type=$type, description=$description, isDone=$isDone, date=${date.time}, superTask=$superTask, subTasks=$subTasks)"
}

fun Iterable<TaskModel>.toEntity(): List<TaskEntity> = map(TaskModel::toEntity)

fun Iterable<TaskModel>.toSuperTaskEntity(): List<SubTaskEntity> = mapNotNull(TaskModel::toSuperTaskEntity)

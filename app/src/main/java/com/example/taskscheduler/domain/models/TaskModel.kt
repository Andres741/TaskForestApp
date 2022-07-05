package com.example.taskscheduler.domain.models

import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperTask
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner.Companion.allToSimpleTaskTitleOwner
import com.example.taskscheduler.util.dataStructures.WrapperList
import java.util.*

//TODO: implement TaskJson.

data class TaskModel (
    val title: String,
    val type: String,
    var description: String = "",
    val superTask: SimpleTaskTitleOwner = SimpleTaskTitleOwner(),
    val subTasks: List<SimpleTaskTitleOwner> = emptyList(),
    var isDone: Boolean = false,
    val dateNum: Long = System.currentTimeMillis(),
): ITaskTypeNameOwner, ITaskTitleOwner {

    val date: Calendar get() = dateNum.let { Calendar.getInstance().apply { timeInMillis = it } }
    val superTaskTitle get() = superTask.taskTitle
    val subTaskTitles: List<String> get() = WrapperList(subTasks, SimpleTaskTitleOwner::taskTitle)
    val hasDescription get() = description.isNotBlank()
    val hasSuperTask get() = superTask.isNotBlank()
    val hasSubTasks get() = subTasks.isNotEmpty()
    val hasFamily get() = hasSubTasks || hasSuperTask
    val numSubTasks get() = subTasks.size
    override val typeName: String get() = type
    override val taskTitle: String get() = title

    constructor(entity: TaskWithSuperAndSubTasks): this (
        title = entity.task.title, type = entity.task.type, description = entity.task.description,
        isDone = entity.task.isDone, dateNum = entity.task.date,
        superTask = SimpleTaskTitleOwner(entity.superTaskEntity),
        subTasks = allToSimpleTaskTitleOwner(entity.subTaskEntities),
    )

    fun toEntity() = TaskEntity(
        title = title, type = type, description = description, isDone = isDone, date = dateNum
    )

    /**Returns a SubTaskEntity with the relationship of hierarchy whit its father, or null if does not have father.*/
    fun toSuperTaskEntity() =
        if (hasSuperTask) SubTaskEntity(superTask = superTask.taskTitle, subTask = title)
        else SubTaskEntity(title)

    fun toTaskEntities() = toEntity() to toSuperTaskEntity()

    /**Returns a List of SubTaskEntity with the relationships of hierarchy whit its children.*/
    fun toSubTasksEntities() = subTasks.map { subTask ->
        SubTaskEntity(superTask = title, subTask = subTask.taskTitle)
    }

    override fun toString() =
        "TaskModel(title=$title, type=$type, description=$description, isDone=$isDone, date=${date.time}, superTask=$superTask, subTasks=$subTasks)"
}

fun Iterable<TaskModel>.toEntity(): List<TaskEntity> = map(TaskModel::toEntity)

fun Iterable<TaskModel>.toSuperTaskEntity(): List<SubTaskEntity> = map(TaskModel::toSuperTaskEntity)

fun Iterable<TaskModel>.toTaskEntities() = map(TaskModel::toTaskEntities)


sealed interface ITaskTitleOwner {
    val taskTitle: String

    fun toSimpleTaskTitleOwner() = SimpleTaskTitleOwner(this)
}
infix fun ITaskTitleOwner.equalsTitle(other: ITaskTitleOwner) = taskTitle == other.taskTitle
infix fun ITaskTitleOwner.notEqualsTitle(other: ITaskTitleOwner) = taskTitle != other.taskTitle


/**Only use the primary constructor in the use cases.*/
@JvmInline
value class SimpleTaskTitleOwner constructor(
    override val taskTitle: String
): ITaskTitleOwner {
    constructor(taskTitleOwner: ITaskTitleOwner): this(taskTitleOwner.taskTitle)
    constructor(superTask: SubTaskEntity?): this(superTask?.superTask ?: "")
    constructor(): this("")

    override fun toSimpleTaskTitleOwner() = this

    fun isNotBlank() = taskTitle.isNotBlank()
    fun isBlank() = taskTitle.isBlank()

    companion object {
        fun allToSimpleTaskTitleOwner(subTaskEntities: Iterable<SubTaskEntity>) = subTaskEntities.map {
            SimpleTaskTitleOwner(it.subTask)
        }
    }
}

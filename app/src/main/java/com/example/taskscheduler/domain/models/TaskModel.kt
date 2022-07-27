package com.example.taskscheduler.domain.models

import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.remote.netClases.TaskDocument
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner.Companion.allToSimpleTaskTitleOwner
import com.example.taskscheduler.util.dataStructures.BDTree
import com.example.taskscheduler.util.dataStructures.MyLinkedList
import com.example.taskscheduler.util.dataStructures.WrapperList
import java.util.*

data class TaskModel (
    val title: String,
    val type: String,
    val description: String = "",
    val superTask: SimpleTaskTitleOwner = SimpleTaskTitleOwner(),
    val subTasks: List<SimpleTaskTitleOwner> = emptyList(),
    var isDone: Boolean = false,
    val dateNum: Long = System.currentTimeMillis(),
    val adviseDate: Long?
): ITaskTypeNameOwner, ITaskTitleOwner {

    val date: Calendar get() = Calendar.getInstance().apply { timeInMillis = dateNum }
    val superTaskTitle get() = superTask.taskTitle
    val subTaskTitles: List<String> get() = subTasks.asStringList()
    val hasDescription get() = description.isNotBlank()
    val hasSuperTask get() = superTask.isNotBlank()
    val hasSubTasks get() = subTasks.isNotEmpty()
    val hasFamily get() = hasSubTasks || hasSuperTask
    val numSubTasks get() = subTasks.size
    override val typeName: String get() = type
    override val taskTitle: String get() = title

    constructor(entity: TaskWithSuperAndSubTasks): this (
        title = entity.task.title, type = entity.task.type, description = entity.task.description,
        isDone = entity.task.isDone, dateNum = entity.task.date, adviseDate = entity.task.adviseDate,
        superTask = SimpleTaskTitleOwner(entity.superTaskEntity),
        subTasks = allToSimpleTaskTitleOwner(entity.subTaskEntities),
    )

    fun toEntity() = TaskEntity(
        title = title, type = type, description = description, isDone = isDone, date = dateNum, adviseDate = adviseDate
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

    fun toDocument() = TaskDocument (
        title = title, type = type, description = description,
        done = isDone, dateNum = dateNum, adviseDate = adviseDate,
        superTask = superTaskTitle,
        subTasks = subTaskTitles
    )

    override fun toString() = "TaskModel(title=$title, type=$type, description=$description, " +
            "isDone=$isDone, dateNum=${dateNum}, superTaskTitle=$superTaskTitle, subTaskTitles=$subTaskTitles)"
}

fun Iterable<TaskModel>.toEntity(): List<TaskEntity> = map(TaskModel::toEntity)

fun Iterable<TaskModel>.toSuperTaskEntity(): List<SubTaskEntity> = map(TaskModel::toSuperTaskEntity)

fun Iterable<TaskModel>.toTaskEntities() = map(TaskModel::toTaskEntities)

fun Iterable<TaskModel>.asTaskEntitiesSeq() = asSequence().map(TaskModel::toTaskEntities)

fun Iterable<TaskModel>.toDocument() = map(TaskModel::toDocument)

fun Iterable<TaskModel>.asDocumentSeq() = asSequence().map(TaskModel::toDocument)

fun Iterable<TaskModel>.toMap(): Map<SimpleTaskTitleOwner, TaskModel> = mutableMapOf<SimpleTaskTitleOwner, TaskModel>().also { map ->
    forEach { task ->
        map[task.toSimpleTaskTitleOwner()] = task
    }
}

fun Iterable<TaskModel>.toTrees() = MyLinkedList<BDTree<TaskModel>>().also { fathers ->
    val children = hashMapOf<String, TaskModel>()
    forEach { task ->
        if (task.hasSuperTask) children[task.title] = task
        else fathers.add(BDTree(task))
    }
    fathers.forEach { topTaskTree ->
        topTaskTree.makeTreeWith(children)
    }
}

/**Warning: this function removes elements from the map, and the map has to contain all the child tasks.*/
private fun BDTree<TaskModel>.makeTreeWith(map: MutableMap<String, TaskModel>) {
    value.subTaskTitles.asSequence().mapNotNull(map::remove).forEach { task ->
        addChild(task).apply { makeTreeWith(map) }
    }
}


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
fun String.toTaskTitle() = SimpleTaskTitleOwner(this)  //TODO: delete?

fun Iterable<String>.toTaskTitle() = map(String::toTaskTitle)

fun List<SimpleTaskTitleOwner>.asStringList() = WrapperList(this, SimpleTaskTitleOwner::taskTitle)

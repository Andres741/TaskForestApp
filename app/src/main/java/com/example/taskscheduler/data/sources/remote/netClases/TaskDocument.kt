package com.example.taskscheduler.data.sources.remote.netClases

import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel

data class TaskDocument(
    val title: String? = null,
    val type: String? = null,
    val description: String? = null,
    val superTask: String? = null,
    val subTasks: List<String>? = null,
//    @field:JvmField // -> isDone
    val done: Boolean? = null,
    val dateNum: Long? = null,
    val adviseDate: Long? = null,

    val deleted: Boolean = false
): IFirestoreDocument {

    override fun obtainDocumentName() = title ?: ""

    fun containsPropertyShouldNotBeNull() = title == null || type == null || description == null ||
            superTask == null || subTasks == null || done == null || dateNum == null

    fun toModel() = TaskModel(
        title = title!!, type = type!!, description = description!!,
        isDone = done!!, dateNum = dateNum!!, adviseDate = adviseDate,
        superTask = superTask!!.let(::SimpleTaskTitleOwner),
        subTasks = subTasks!!.map(::SimpleTaskTitleOwner),
    )

    fun toModelOrNull(): TaskModel? {
        if (deleted) return null
        return TaskModel (
            title = title ?: return null, type = type ?: return null, description = description ?: return null,
            isDone = done ?: return null, dateNum = dateNum ?: return null, adviseDate = adviseDate,
            superTask = (superTask ?: return null).let(::SimpleTaskTitleOwner),
            subTasks = (subTasks ?: return null).map(::SimpleTaskTitleOwner),
        )
    }
}

fun Iterable<TaskDocument>.toModel() = mapNotNull(TaskDocument::toModelOrNull)

fun Iterable<TaskDocument>.tryToModel() = map(TaskDocument::toModel)

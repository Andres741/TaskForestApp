package com.example.taskscheduler.data.sources.remote.netClases

import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.google.gson.annotations.SerializedName

data class TaskJson(
    @SerializedName(value = "title")
    val title: String? = null,
    @SerializedName(value = "type")
    val type: String? = null,
    @SerializedName(value = "description")
    val description: String? = null,
    @SerializedName(value = "super_task")
    val superTask: String? = null,
    @SerializedName(value = "sub_tasks")
    val subTasks: List<String>? = null,
//    @field:JvmField // -> isDone
    @SerializedName(value = "is_done")
    val done: Boolean? = null,
    @SerializedName(value = "date_num")
    val dateNum: Long? = null,
): IFirestoreDocument {

    override fun obtainDocumentName() = title ?: ""

    fun containsNullProperty() = title == null || type == null || description == null ||
            superTask == null || subTasks == null || done == null || dateNum == null

    fun toModel() = TaskModel(
        title = title!!, type = type!!, description = description!!,
        isDone = done!!, dateNum = dateNum!!,
        superTask = superTask!!.let(::SimpleTaskTitleOwner),
        subTasks = subTasks!!.map(::SimpleTaskTitleOwner),
    )

    fun toModelOrNull(): TaskModel? {
        return if (containsNullProperty()) null
        else toModel()
    }
}

fun Iterable<TaskJson>.toModel() = mapNotNull(TaskJson::toModelOrNull)

fun Iterable<TaskJson>.toModelOrException() = map(TaskJson::toModel)

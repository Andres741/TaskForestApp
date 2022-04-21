package com.example.taskscheduler.data.models

//TODO: implement TaskEntity and TaskJson.

data class TaskModel(
    val title: String,
    val type: String,
    val description: String = "",
    val subTasks: MutableList<TaskModel> = mutableListOf(),
) {
    var isDone: Boolean = false

    val hasDescription get() = description.isNotBlank()
    val hasSubTasks get() = subTasks.isNotEmpty()
    val numSubTasks get() = subTasks.size
}

package com.example.taskscheduler.data.models

import com.example.taskscheduler.data.sources.local.entities.TaskEntity
import com.example.taskscheduler.util.dataStructures.BDTree

//TODO: implement TaskJson.

class TaskModel (
    val title: String,
    val type: String,
    val description: String = "",
    superTask: TaskModel? = null,
    subTasks: List<TaskModel> = emptyList(),
) {
    private val tree: BDTree<TaskModel> = BDTree(this)

    private val _superTask: BDTree<TaskModel>?
        get() = tree.father
    var superTask: TaskModel?
        get() = _superTask?.value
        set(value) {
            tree.father = value?.let { BDTree(value) }
        }

    private val _subTasks: List<BDTree<TaskModel>>
        get() =  tree.children
    val subTasks: List<TaskModel>
        get() = _subTasks.map{it.value}

    init {
        this.superTask = superTask
        tree.addChildren(subTasks)
    }

    var isDone: Boolean = false

    val hasDescription get() = description.isNotBlank()
    val hasSubTasks get() = _subTasks.isNotEmpty()
    val numSubTasks get() = _subTasks.size

    fun toEntityWithSubTasks(): List<TaskEntity> = tree.toList().toEntity()
    fun toEntityAll(): List<TaskEntity> = tree.toListAll().toEntity()
    fun toEntity() = TaskEntity(
        title = title, type = type, description = description,
//        superTask = superTask?.title ?: "",// subTasks = subTasks.map { it.title }
    )
}

fun List<TaskModel>.toEntity(): List<TaskEntity> = map { it.toEntity() }

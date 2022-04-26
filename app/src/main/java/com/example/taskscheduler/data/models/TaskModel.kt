package com.example.taskscheduler.data.models

import com.example.taskscheduler.data.sources.local.entities.TaskEntity
import com.example.taskscheduler.util.dataStructures.BDTree
import com.example.taskscheduler.util.dataStructures.Tree

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
            tree.father = if (value == null) null else BDTree(value)
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
    fun toEntity(): TaskEntity {
        return TaskEntity(title, type, description)
    }
}

fun List<TaskModel>.toEntity(): List<TaskEntity> = map { it.toEntity() }

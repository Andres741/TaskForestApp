package com.example.taskscheduler.data.sources.local.taskTree

import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.toTaskTitle
import com.example.taskscheduler.domain.models.toTrees
import com.example.taskscheduler.util.dataStructures.BDTree
import com.example.taskscheduler.util.dataStructures.MyLinkedList

class TaskForest() {
    private val _rootsSet = mutableSetOf<String>()
    private val _taskMap = mutableMapOf<String, BDTree<TaskEntity>>()

    val rootsSet: Set<String> = _rootsSet
    val taskMap: Map<String, BDTree<TaskEntity>> = _taskMap

    val roots get() = _rootsSet.toList()

    val treesRoots: List<BDTree<TaskEntity>> get() = _rootsSet.map { rootTitle ->
        _taskMap[rootTitle]!!
    }

    constructor(tasks: Iterable<TaskModel>): this() {
        val trees = tasks.toTrees()
        trees.forEach { tree ->
            _rootsSet.add(tree.value.title)

            tree.map { taskModel ->
                taskModel.toEntity()
            }.forEachBDTree { branch ->
                _taskMap[branch.value.title] = branch
            }
        }
    }

    fun add(task: TaskModel): Boolean {
        val taskTitle = task.title
        _taskMap[taskTitle]?.also { return false }

        val fatherTree = _taskMap[task.superTaskTitle]

        if (! task.hasSuperTask || fatherTree == null ) {
            _rootsSet.add(taskTitle)
            _taskMap[taskTitle] = BDTree(task.toEntity())
            return true
        }

        _taskMap[taskTitle] = fatherTree.addChild(task.toEntity())
        return true
    }

//    /**Returns a set with the task titles added.*/
//    fun addAll(task: Iterable<TaskModel>): Set<String> = mutableSetOf<String>().also { set ->
//        task.forEach { task ->
//            if (add(task)) set.add(task.title)
//        }
//    }

    /**Returns a set with the task titles added.*/
    fun addAll(tasks: Iterable<TaskModel>) = tasks.asSequence().mapNotNull { task ->
        if (add(task)) task.title else null
    }.toSet()

    /**The first iterable stores the saved tasks, and second those that do not*/
    fun addAllAndDistinct(tasks: Iterable<TaskModel>): Pair<Iterable<String>, Iterable<String>> {
        val saved = MyLinkedList<String>()
        val notSaved = MyLinkedList<String>()

        tasks.forEach { task ->
            if (add(task))
                saved.add(task.title)
            else
                notSaved.add(task.title)
        }
        return saved.asIterable() to notSaved.asIterable()
    }

    fun get(title: String): TaskModel? {
        val taskTree = _taskMap[title] ?: return null
        return taskTree.toModel()
    }

    fun getAllIn(titles: Iterable<String>) = titles.mapNotNull(::get)


    fun toList(): List<TaskModel> = mutableListOf<TaskModel>().also { list ->
        _taskMap.forEach { (_, tree) ->
            list.add(tree.toModel())
        }
    }

    override fun toString() = buildString {
        append(super.toString())
        append("\n")
        taskMap.entries.forEachIndexed { index, (title, tree) ->
            append("$index: $title=${tree.toModel()}\n")
        }
    }
}

fun BDTree<TaskEntity>.toModel() = TaskModel(
    title = value.title, type = value.type, description = value.description,
    isDone = value.isDone, dateNum = value.date,
    superTask = SimpleTaskTitleOwner(father?.value?.title ?: ""),  //__NULL__
    subTasks = childrenIter.asSequence().map { child -> child.value.title }.asIterable().toTaskTitle(),
)

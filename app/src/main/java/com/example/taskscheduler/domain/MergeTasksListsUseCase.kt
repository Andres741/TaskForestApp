package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.taskTree.TaskForest
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.dataStructures.MyLinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergeTasksListsUseCase @Inject constructor() {
    operator fun invoke(
        priorityList: Iterable<TaskModel>, notPriorityList: Iterable<TaskModel>
    ): List<TaskModel> = MyLinkedList<TaskModel>().apply {

        val notPriorityMap: MutableMap<String, TaskModel> = notPriorityList.let { iter ->
            mutableMapOf<String, TaskModel>().also { map->
                iter.forEach { task ->
                    map[task.title] = task
                }
            }
        }
        priorityList.forEach { priorityTask ->
            add(priorityTask)
            notPriorityMap.remove(priorityTask.title)
        }
        notPriorityMap.forEach { (_, task) ->
            add(task)
        }
    }

    fun smart(
        priorityList: Iterable<TaskModel>, notPriorityList: Iterable<TaskModel>,
    ): MergeTasksListsResponse {
        val addToPriorityList = MyLinkedList<TaskModel>()
        val addToNotPriorityList = MyLinkedList<TaskModel>()

        val notPriorityMap: MutableMap<String, TaskModel> = notPriorityList.let { iter ->
            mutableMapOf<String, TaskModel>().also { map->
                iter.forEach { task ->
                    map[task.title] = task
                }
            }
        }

        for (priorityTask in priorityList) {
            val taskAlsoInNotPriority = notPriorityMap.remove(priorityTask.title)
            if (taskAlsoInNotPriority != null || taskAlsoInNotPriority == priorityTask) continue
            addToNotPriorityList.add(priorityTask)
        }

        notPriorityMap.forEach { (_, task) ->
            addToPriorityList.add(task)
        }

        return MergeTasksListsResponse(
            addToPriorityList.asIterable(),
            addToNotPriorityList.asIterable(),
        )
    }

    fun withTree(
        priorityList: Iterable<TaskModel>, notPriorityList: Iterable<TaskModel>,
    ): MergeTasksListsResponse {
        val taskTree = TaskForest(priorityList)
        val addToPrior = MyLinkedList<TaskModel>()
        val addToNotPrior = MyLinkedList<TaskModel>()

        notPriorityList.forEach { notPrior ->
            if (taskTree.add(notPrior))
                addToPrior.add(notPrior)
            else
                addToNotPrior.add(taskTree.get(notPrior.title)!!)
        }

        return MergeTasksListsResponse(
            addToPrior.asIterable(),
            addToNotPrior.asIterable(),
        )
    }

    data class MergeTasksListsResponse (
        val addToPriority: Iterable<TaskModel>,
        val addToNotPriority: Iterable<TaskModel>,
    )
}


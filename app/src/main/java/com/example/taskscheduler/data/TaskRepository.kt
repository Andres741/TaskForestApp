package com.example.taskscheduler.data

import android.util.Log
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasks
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.toDocument
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class TaskRepository(
    private val local: ILocalTaskRepository,
    private val firestoreTasks: FirestoreTasks,
): ITaskRepository by local {
    private val easyFiresoreSynchronizationScopeProvider = OneScopeAtOnceProvider(Dispatchers.Default)
    val isEasyFireSoreSynchronizationWorking get() = easyFiresoreSynchronizationScopeProvider.currentScope != null

    //TODO: check when write methods of firestoreTasks fails.

    //Create
    override suspend fun saveNewTask(newTask: TaskModel) = coroutineScope {
        launch {
            val taskDocument = newTask.toDocument()
            firestoreTasks.save(taskDocument)
        }
        launch {
            val taskTitle = newTask.title
            val superTask = newTask.superTaskTitle
            firestoreTasks.addSubTask(taskTitle, superTask)
        }
        //Useless to save newTask.subTaskTitles, because new task doesn't have subtasks.
        local.saveNewTask(newTask)
    }

    //Update
    override suspend fun changeDone(task: ITaskTitleOwner, newValue: Boolean) = coroutineScope {
        launch {
            firestoreTasks.setTaskIsDone(task.taskTitle, newValue)
        }
        local.changeDone(task, newValue)
    }

    override suspend fun changeTaskDescription(task: ITaskTitleOwner, newValue: String) = coroutineScope {
        launch {
            firestoreTasks.setTaskDescription(task.taskTitle, newValue)
        }
        local.changeTaskDescription(task, newValue)
    }

    override suspend fun changeTaskTitle(task: ITaskTitleOwner, newValue: String): Boolean {
        return local.changeTaskTitle(task, newValue).ifTrue {
            val previousTaskTitle = task.taskTitle
            val changedTask = local.getTaskByTitleStatic(newValue)

            withContext(Dispatchers.Default + Job()) {
                launch {
                    changedTask.subTasks.forEach {
                        launch {
                            val subTaskTitle = it.taskTitle
                            firestoreTasks.setSupertask(
                                superTask = newValue, itsSubTask = subTaskTitle
                            )
                        }
                    }
                }
                launch {
                    firestoreTasks.delete(previousTaskTitle)
                    firestoreTasks.save(changedTask.toDocument())
                }
                firestoreTasks.removeSubTask(
                    subTask = previousTaskTitle, itsSuperTask = changedTask.superTaskTitle
                )
                firestoreTasks.addSubTask(
                    subTask = changedTask.taskTitle, itsSuperTask = changedTask.superTaskTitle
                )
            }
        }
    }

    override suspend fun changeType(newValue: String, oldValue: String) =
        local.changeType(newValue.log("newValue"), oldValue.log("oldValue")).ifTrue {
            local.getTaskTitlesByTypeStatic(newValue).logList("---modified Task Title---").forEach { modifiedTaskTitle ->
                firestoreTasks.setType(newValue, modifiedTaskTitle)
            }
        }

    override suspend fun changeTypeInTaskHierarchy(task: String, newValue: String) =
        local.changeTypeInTaskHierarchy(task, newValue).ifTrue {
            local.getTitlesOfHierarchyOfTaskByTypeStatic(newValue).forEach { modifiedTaskTitle ->
                firestoreTasks.setType(newValue, modifiedTaskTitle)
            }
        }

    //Delete
    override suspend fun deleteSingleTask(task: ITaskTitleOwner): Boolean {
        val deletedTaskTitle = task.taskTitle
        val deletedTask = local.getTaskByTitleStatic(deletedTaskTitle)  //Don't move from here

        return local.deleteSingleTask(task).ifTrue {
            withContext(Dispatchers.Default + Job()) {
                launch {
                    val superTaskTitle = deletedTask.superTaskTitle
                    deletedTask.subTasks.forEach {
                        val subTaskTitle = it.taskTitle
                        launch {
                            firestoreTasks.setSupertask(
                                superTask = superTaskTitle, itsSubTask = subTaskTitle
                            )
                        }
                        firestoreTasks.addSubTask(subTaskTitle, superTaskTitle)
                    }
                }
                launch {
                    firestoreTasks.delete(deletedTaskTitle)
                }
                firestoreTasks.removeSubTask(
                    subTask = deletedTaskTitle, itsSuperTask = deletedTask.superTaskTitle
                )
            }
        }
    }

    override suspend fun deleteTaskAndAllChildren(task: ITaskTitleOwner): Boolean {
        if (local.existsTitle(task.taskTitle).not()) return false

        return withContext(Dispatchers.Default + Job()) {
            launch {
                firestoreTasks.delete(task.taskTitle)
            }

            val superTaskTitleDef = async {
                val superTaskTitle = local.getSuperTaskTitleStatic(task)
                superTaskTitle.takeIf { it != ""}
            }
            launch {
                val superTaskTitle = superTaskTitleDef.await() ?: return@launch
                firestoreTasks.removeSubTask(task.taskTitle, superTaskTitle)
            }

            val allSubTasksTitles = local.getAllChildrenTitlesStatic(task).takeIf(List<*>::isNotEmpty).also {
                launch {
                    superTaskTitleDef.join()
                    if (it == null) {
                        local.deleteSingleTask(task)
                    } else {
                        local.deleteTaskAndAllChildren(task)
                    }
                }
            }

            allSubTasksTitles?.apply {
                forEach { subtaskTitle ->
                    launch {
                        firestoreTasks.delete(subtaskTitle)
                    }
                }
            }
            true
        }
    }

    override suspend fun deleteAll() = coroutineScope {
        val titles = local.getAllTasksTitlesStatic().takeIf(List<*>::isNotEmpty) ?: return@coroutineScope false
        launch {
            local.deleteAll()
        }
        firestoreTasks.deleteAll(titles)
        true
    }



    fun initEasyFiresoreSynchronization(): Boolean {
        val firestoreTasks = firestoreTasks
        return null != easyFiresoreSynchronizationScopeProvider.newScopeNotCancelCurrentOrNull?.launch {
            local.getAllTasks().collectLatest {

                firestoreTasks.getAllTasks().fold({ tasks ->
                    tasks.logList("\n\n---tasks in firestore---".uppercase())
                }){ throwable ->
                    throwable.log("\n\nSearching tasks in firestore failed".uppercase())
                }

                firestoreTasks.deleteAllTasks()

                val newTasks = it.toDocument()

                "\nNew tasks".log()
                newTasks.forEach { task ->
                    task.log()
                }

                firestoreTasks.saveAll(newTasks).fold ({
                    "---Tasks has been saved---"
                }) { throwable ->
                    "--Saving tasks failed: $throwable"
                }.log()
            }
        }
    }

    fun finishEasyFireSoreSynchronization() {
        easyFiresoreSynchronizationScopeProvider.cancel()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("TaskRepository", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
        "$msj:".uppercase().log()
        this.iterator().hasNext().takeIf { it } ?: kotlin.run {
            "  Collection is empty".log()
            return@apply
        }
        forEachIndexed { index, elem ->
            elem.log(index)
        }
    }
}

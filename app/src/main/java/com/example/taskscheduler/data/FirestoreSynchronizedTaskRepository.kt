package com.example.taskscheduler.data

import android.util.Log
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.data.sources.local.taskTree.TaskForest
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasks
import com.example.taskscheduler.data.sources.remote.netClases.toModel
import com.example.taskscheduler.domain.models.*
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.ifTrue
import com.example.taskscheduler.util.notContains
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException

class FirestoreSynchronizedTaskRepository(
    private val local: ILocalTaskRepository,
    private val firestoreTasks: FirestoreTasks,
): ITaskRepository by local {
    //TODO: check when write methods of firestoreTasks fails.
    val easyFiresoreSynchronization = lazy { EasyFiresoreSynchronization() }

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

    suspend fun replaceTasksWithNew(tasks: Iterable<TaskModel>, oldTaskTitlesInFirestore: Iterable<String>) {
        if (tasks.iterator().hasNext().not()) return
        firestoreTasks.deleteAll(oldTaskTitlesInFirestore)
        
        coroutineScope {
            launch {
                firestoreTasks.saveAll(tasks.asDocumentSeq().asIterable())
            }
            local.saveAll(tasks)
        }
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

            coroutineScope {
                launch {
                    changedTask.subTasks.forEach {
                        launch(Dispatchers.IO) {
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
        local.changeType(newValue, oldValue).ifTrue {
            coroutineScope {
                local.getTaskTitlesByTypeStatic(newValue).forEach { modifiedTaskTitle ->
                    launch(Dispatchers.IO) {
                        firestoreTasks.setType(newValue, modifiedTaskTitle)
                    }
                }
            }
        }

    override suspend fun changeTypeInTaskHierarchy(task: String, newValue: String) =
        local.changeTypeInTaskHierarchy(task, newValue).ifTrue {
            coroutineScope {
                local.getTitlesOfHierarchyOfTaskByTypeStatic(newValue).forEach { modifiedTaskTitle ->
                    launch(Dispatchers.IO) {
                        firestoreTasks.setType(newValue, modifiedTaskTitle)
                    }
                }
            }
        }

    //Delete
    override suspend fun deleteSingleTask(task: ITaskTitleOwner): Boolean {
        val deletedTaskTitle = task.taskTitle
        val deletedTask = local.getTaskByTitleStatic(deletedTaskTitle)  //Don't move from here

        return local.deleteSingleTask(task).ifTrue {
            coroutineScope {
                launch {
                    val superTaskTitle = deletedTask.superTaskTitle
                    deletedTask.subTasks.forEach {
                        val subTaskTitle = it.taskTitle
                        launch(Dispatchers.IO) {
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

        return coroutineScope {
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
                    launch(Dispatchers.IO) {
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

    private suspend fun saveAllOnlyInFirebase(tasks: Iterable<TaskModel>) {
        firestoreTasks.saveAll(tasks.asDocumentSeq().asIterable())
    }

    private suspend fun saveOnlyInLocal(tasks: Iterable<TaskModel>) {
        local.saveAll(tasks)
    }

    suspend fun getAllFromFirebase() = firestoreTasks.getAllTasks().fold({ documents ->
        documents.toModel()
    }) { t ->
        t.log("Not possible to connect with firestore because")
        throw IOException("Firestore does not respond", t)
    }

    suspend fun mergeLists(): Unit = coroutineScope {
        val allFromFirebaseDef = async {
            withTimeout(5000){
                getAllFromFirebase()
            }
        }
        val allFromLocal = local.getAllTasksStatic()

        val forest = TaskForest(allFromLocal)

        val addToLocal = forest.addAll(allFromFirebaseDef.await())

        launch {
            val addToRemote = forest.taskMap.keys.asSequence().filter(addToLocal::notContains).asIterable()
            saveAllOnlyInFirebase(forest.getAllIn(addToRemote))
        }
        saveOnlyInLocal(forest.getAllIn(addToLocal))
    }

//    suspend fun mergeListsRemote(): Unit = coroutineScope {
//        val allFromLocalDef = async { local.getAllTasksStatic() }
//
//        val allFromFirebase = withTimeout(5000) {
//            getAllFromFirebase()
//        }
//
//        val forest = TaskForest(allFromFirebase)
//
//        val addToRemote = forest.addAll(allFromLocalDef.await())
//
//        launch {
//            val addToLocal = forest.taskMap.keys.asSequence().filter(addToRemote::notContains).asIterable()
//            saveOnlyInLocal(forest.getAllIn(addToLocal))
//        }
//        saveAllOnlyInFirebase(forest.getAllIn(addToRemote))
//    }


    inner class EasyFiresoreSynchronization {
        private val easyFiresoreSynchronizationScopeProvider = OneScopeAtOnceProvider(Dispatchers.Default)
        val isEasyFireSoreSynchronizationWorking get() = easyFiresoreSynchronizationScopeProvider.currentScope != null

        fun initEasyFiresoreSynchronization(): Boolean {
            val firestoreTasks = firestoreTasks
            return null != easyFiresoreSynchronizationScopeProvider.newScopeNotCancelCurrentOrNull?.launch {
                local.getAllTasks().collectLatest {

//                    firestoreTasks.getAllTasks().fold({ tasks ->
//                        tasks.logList("\n\n---tasks in firestore---".uppercase())
//                    }){ throwable ->
//                        throwable.log("\n\nSearching tasks in firestore failed".uppercase())
//                    }

                    firestoreTasks.deleteAllTasks()

                    val newTasks = it.toDocument().logList("New tasks")


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
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("FirestoreSynchronizedTaskRepository", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.bigLog(msj: Any? = null) = apply  {
        "".log(); toString().uppercase().log(msj); "".log()
    }
    private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
        "$msj:".uppercase().log()
        this.iterator().hasNext().ifFalse {
            "  Collection is empty".log()
            return@apply
        }
        forEachIndexed { index, elem ->
            elem.log(index)
        }
    }
}

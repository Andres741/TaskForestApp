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
import com.google.firebase.firestore.Source
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException
import kotlin.system.measureTimeMillis

class FirestoreSynchronizedTaskRepository(
    private val local: ILocalTaskRepository,
    private val firestoreTasks: FirestoreTasks,
): ITaskRepository by local {
    //TODO: check when write methods of firestoreTasks fails.
    val easyFiresoreSynchronization = lazy { EasyFiresoreSynchronization() }

    //Create
    override suspend fun saveNewTask(newTask: TaskModel) = run {
        CoroutineScope(Dispatchers.IO).launch {
            val taskDocument = newTask.toDocument()
            firestoreTasks.save(taskDocument)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val taskTitle = newTask.title
            val superTask = newTask.superTaskTitle
            firestoreTasks.addSubTask(taskTitle, superTask)
        }
        //Useless to save newTask.subTaskTitles, because new task doesn't have subtasks.
        local.saveNewTask(newTask).also { "Local task saved".log() }
    }

    suspend fun replaceTasksWithNew(tasks: Iterable<TaskModel>, oldTaskTitlesInFirestore: Iterable<String>) {
        if (tasks.iterator().hasNext().not()) return

        CoroutineScope(Dispatchers.IO).launch {
            firestoreTasks.deleteAll(oldTaskTitlesInFirestore)
            firestoreTasks.saveAll(tasks.asDocumentSeq().asIterable())
        }
        local.saveAll(tasks)
    }

    //Update
    override suspend fun changeDone(task: ITaskTitleOwner, newValue: Boolean) = run {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreTasks.setTaskIsDone(task.taskTitle, newValue)
        }
        local.changeDone(task, newValue)
    }

    override suspend fun changeTaskDescription(task: ITaskTitleOwner, newValue: String) = run {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreTasks.setTaskDescription(task.taskTitle, newValue)
        }
        local.changeTaskDescription(task, newValue)
    }

    override suspend fun changeTaskTitle(task: ITaskTitleOwner, newValue: String): Boolean {
        return local.changeTaskTitle(task, newValue).ifTrue {
            val previousTaskTitle = task.taskTitle
            val changedTask = local.getTaskByTitleStatic(newValue)

            CoroutineScope(Dispatchers.Default).launch {
                changedTask.subTasks.forEach {
                    launch(Dispatchers.IO) {
                        val subTaskTitle = it.taskTitle
                        firestoreTasks.setSupertask(
                            superTask = newValue, itsSubTask = subTaskTitle
                        )
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                firestoreTasks.delete(previousTaskTitle)
                firestoreTasks.save(changedTask.toDocument())
            }
            CoroutineScope(Dispatchers.IO).launch {
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
            local.getTaskTitlesByTypeStatic(newValue).forEach { modifiedTaskTitle ->
                CoroutineScope(Dispatchers.IO).launch {
                    firestoreTasks.setType(newValue, modifiedTaskTitle)
                }
            }
        }

    override suspend fun changeTypeInTaskHierarchy(task: String, newValue: String) =
        local.changeTypeInTaskHierarchy(task, newValue).ifTrue {
            local.getTitlesOfHierarchyOfTaskByTypeStatic(newValue).forEach { modifiedTaskTitle ->
                CoroutineScope(Dispatchers.IO).launch {
                    firestoreTasks.setType(newValue, modifiedTaskTitle)
                }
            }
        }

    override suspend fun changeAdviseDate(task: String, newValue: Long?) = run {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreTasks.setAdviseDate(task, newValue)
        }
        local.changeAdviseDate(task, newValue)
    }


    //Delete
    override suspend fun deleteSingleTask(task: ITaskTitleOwner): Boolean {
        val deletedTaskTitle = task.taskTitle
        val deletedTask = local.getTaskByTitleStatic(deletedTaskTitle)  //Don't move from here

        return local.deleteSingleTask(task).ifTrue {
            CoroutineScope(Dispatchers.Default).launch {
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
            CoroutineScope(Dispatchers.IO).launch {
                firestoreTasks.delete(deletedTaskTitle)
            }
            CoroutineScope(Dispatchers.IO).launch {
                firestoreTasks.removeSubTask(
                    subTask = deletedTaskTitle, itsSuperTask = deletedTask.superTaskTitle
                )
            }
        }
    }

    override suspend fun deleteTaskAndAllChildren(task: ITaskTitleOwner): Boolean {
        return deleteTaskAndAllChildrenGettingDeleted(task).isNotEmpty()
    }

    override suspend fun deleteTaskAndAllChildrenGettingDeleted(task: ITaskTitleOwner): List<ITaskTitleOwner> {
        if (local.existsTitle(task.taskTitle).not()) return emptyList()

        return coroutineScope {
            CoroutineScope(Dispatchers.IO).launch {
                firestoreTasks.delete(task.taskTitle)
            }

            val superTaskTitle = async {
                local.getSuperTaskTitleStatic(task).takeIf(String::isNotBlank)
            }
            CoroutineScope(Dispatchers.IO).launch {
                firestoreTasks.removeSubTask(
                    task.taskTitle, superTaskTitle.await() ?: return@launch
                )
            }

            val allSubTasksTitles = local.getAllChildrenTitlesStatic(task).takeIf(List<*>::isNotEmpty).also { allChildren ->
                "waiting for superTaskTitle".log()
                superTaskTitle.join()
                "superTaskTitle finished".log()
                launch {
                    if (allChildren == null) {
                        local.deleteSingleTask(task)
                        "Deleted task has not children".log()
                    } else {
                        local.deleteTaskAndAllChildren(task)
                        "Deleted task had children".log()
                    }
                }
            }

            allSubTasksTitles?.apply {
                CoroutineScope(Dispatchers.Default).launch {
                    forEach { subtaskTitle ->
                        launch(Dispatchers.IO) {
                            firestoreTasks.delete(subtaskTitle)
                        }
                    }
                }
            }
            allSubTasksTitles?.map(::SimpleTaskTitleOwner) ?: emptyList()
        }
    }

    override suspend fun deleteAll(): Boolean {
        val titles = local.getAllTasksTitlesStatic().takeIf(List<*>::isNotEmpty) ?: return false
        CoroutineScope(Dispatchers.IO).launch {
            firestoreTasks.deleteAll(titles)
        }
        local.deleteAll()
        return true
    }



    //Not inherited
    private suspend fun saveAllOnlyInFirebase(tasks: Iterable<TaskModel>) {
        firestoreTasks.saveAll(tasks.asDocumentSeq().asIterable())
    }

    private suspend fun saveOnlyInLocal(tasks: Iterable<TaskModel>) {
        local.saveAll(tasks)
    }

    suspend fun getAllFromFirebase() = firestoreTasks.getAllTasks(Source.SERVER).fold({ documents ->
        documents.toModel()
    }) { t ->
        t.log("Not possible to connect with firestore because")
        throw IOException("Firestore does not respond", t)
    }

    suspend fun mergeLists(): Set<String> = coroutineScope {
        val allFromFirebaseDef = async {
            withTimeout(12000){
                val res: List<TaskModel>
                val time = measureTimeMillis {
                    res = getAllFromFirebase()
                }
                "Getting from firebase took $time millis".log()
                res
            }
        }
        val allFromLocal = local.getAllTasksStatic()

        val forest = TaskForest(allFromLocal)

        val addToLocal = forest.addAll(allFromFirebaseDef.await())

        CoroutineScope(Dispatchers.Default).launch {
            val addToRemote = forest.taskMap.keys.asSequence().filter(addToLocal::notContains).asIterable()
            saveAllOnlyInFirebase(forest.getAllIn(addToRemote))
        }
        saveOnlyInLocal(forest.getAllIn(addToLocal))

        return@coroutineScope addToLocal
    }


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

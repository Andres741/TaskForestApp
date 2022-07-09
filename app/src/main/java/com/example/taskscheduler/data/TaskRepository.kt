package com.example.taskscheduler.data

import android.util.Log
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasksAuth
import com.example.taskscheduler.domain.models.toJson
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    val local: ILocalTaskRepository,
    firestoreTasksAuth: FirestoreTasksAuth,
    //TODO: crate remote repository
) {
    private val scopeProvider = OneScopeAtOnceProvider(Dispatchers.Default)
    private val firestoreTasks = firestoreTasksAuth.firestoreTasks
    val isFirestoreAvailable = firestoreTasks != null
    val isEasyFireSoreSynchronizationWorking get() = scopeProvider.currentScope != null

    fun initEasyFiresoreSynchronization(): Boolean {
        val firestoreTasks = firestoreTasks ?: return false
        return null != scopeProvider.newScopeNotCancelCurrentOrNull?.launch {
            local.getAllTasks().collectLatest {

                firestoreTasks.getAllTasks().fold({ tasks ->
                    tasks.logIter("\n\n---tasks in firestore---".uppercase())
                }){ throwable ->
                    throwable.log("\n\nSearching tasks in firestore failed".uppercase())
                }

                firestoreTasks.deleteAllTasks()

                val newTasks = it.toJson()

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
        scopeProvider.cancel()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("TaskRepository", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> Iterable<T>.logIter(msj: Any? = null) = apply {
        msj?.apply{"\n$this".log()}
        forEachIndexed { index, elem ->
            elem.log(index)
        }
    }
}

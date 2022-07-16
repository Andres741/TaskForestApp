package com.example.taskscheduler.domain

import android.util.Log
import com.example.taskscheduler.data.FirestoreSynchronizedTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.*
import javax.inject.Inject

class SynchronizeFromFirestoreUseCase constructor(
    private val saveTaskContext: SaveTaskContext,
    private val taskRepository: FirestoreSynchronizedTaskRepository,
) {
    suspend operator fun invoke(): Unit = withContext(saveTaskContext) {
        taskRepository.mergeLists()
    }
}

class SynchronizeFromFirestoreUseCaseAuth @Inject constructor(
    saveTaskContext: SaveTaskContext,
    taskRepository: ITaskRepository,
) {
    val synchronizeFromFirestoreUseCase = (taskRepository as? FirestoreSynchronizedTaskRepository)?.run {
        SynchronizeFromFirestoreUseCase(saveTaskContext, this)
    }
}


private fun <T> T.log(msj: Any? = null) = apply {
    Log.i("SynchronizeFromFirestoreUseCase", "${if (msj != null) "$msj: " else ""}${toString()}")
}
private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
    "$msj:".uppercase().log()
    this.iterator().hasNext().ifTrue {
        "  Collection is empty".log()
        return@apply
    }
    forEachIndexed { index, elem ->
        elem.log(index)
    }
}

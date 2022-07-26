package com.example.taskscheduler.domain

import android.util.Log
import com.example.taskscheduler.data.FirestoreSynchronizedTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.util.ifTrue
import javax.inject.Inject

class SynchronizeFromFirestoreUseCase constructor(
    private val withWriteTaskContext: WithWriteTaskContext,
    private val taskRepository: FirestoreSynchronizedTaskRepository,
    private val adviseDateNotification: AdviseDateNotificationUseCase,
) {
    suspend operator fun invoke() = withWriteTaskContext {
        taskRepository.mergeLists().onEach { addedTaskTitle ->
            val addedTask = taskRepository.getTaskByTitleStatic(addedTaskTitle)
            adviseDateNotification.set(addedTask)
        }
    }
}

class SynchronizeFromFirestoreUseCaseAuth @Inject constructor(
    withWriteTaskContext: WithWriteTaskContext,
    taskRepository: ITaskRepository,
    adviseDateNotification: AdviseDateNotificationUseCase,
) {
    val synchronizeFromFirestoreUseCase = (taskRepository as? FirestoreSynchronizedTaskRepository)?.let { repo ->
        SynchronizeFromFirestoreUseCase(withWriteTaskContext, repo, adviseDateNotification)
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

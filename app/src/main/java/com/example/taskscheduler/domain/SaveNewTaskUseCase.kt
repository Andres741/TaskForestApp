package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SaveNewTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {

    //TODO: decide which one I will use and fix the synchronization in domain layer.
    private val saveTaskContext = newSingleThreadContext("saveTaskThread")// + NonCancellable
    private val saveTaskContext1 = Dispatchers.Default.limitedParallelism(1)// + NonCancellable
    private val saveTaskContext2 = Executors.newSingleThreadExecutor().asCoroutineDispatcher() + NonCancellable

    /**
     * This function must be used as the constructor of TaskModel in the IU layer.
     * Is similar to CreateValidTaskUseCase, but it saves the task into the database.
     * All Successful objects returned by this function are instances of SavedTask.
     * This function is synchronized and non cancellable.
     */
    suspend operator fun invoke(
        title: String?, type: String?, description: String?, superTask: String?

    ): CreateValidTaskUseCase.Response = withContext(saveTaskContext2) {

        createValidTaskUseCase(
            title, type, description, superTask
        ).also { response ->
            if (response !is CreateValidTaskUseCase.Response.Successful) return@also

            taskRepository.local.saveNewTask(response.task)
            return@withContext SavedTask(response)
        }
    }
    class SavedTask(successfulResponse: Successful): CreateValidTaskUseCase.Response.Successful(successfulResponse.task)
}

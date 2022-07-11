package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveNewTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val saveTaskContext: SaveTaskContext,
) {
    /**
     * This function must be used as the constructor of TaskModel in the IU layer.
     * Is similar to CreateValidTaskUseCase, but it saves the task into the database.
     * All Successful objects returned by this function are instances of SavedTask.
     * This function is synchronized and non cancellable.
     */
    suspend operator fun invoke(
        title: String?, type: String?, description: String?, superTask: String?
    ): CreateValidTaskUseCase.Response = withContext(saveTaskContext) {
        createValidTaskUseCase(
            title, type, description, superTask
        ).also { response ->
            if (response !is ValidTask) return@also

            taskRepository.saveNewTask(response.task)
            return@withContext SavedTask(response)
        }
    }
    class SavedTask(validTaskResponse: ValidTask): ValidTask(validTaskResponse.task)
}

typealias SavedTask = SaveNewTaskUseCase.SavedTask

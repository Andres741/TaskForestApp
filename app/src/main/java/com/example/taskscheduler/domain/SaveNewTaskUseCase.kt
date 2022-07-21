package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.domain.synchronization.WriteTaskContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveNewTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
) {
    /**
     * This function must be used as the constructor of TaskModel in the IU layer.
     * Is similar to CreateValidTaskUseCase, but it saves the task into the database.
     * All Successful objects returned by this function are instances of SavedTask.
     * This function is synchronized and non cancellable.
     */
    suspend operator fun invoke(
        title: String?, type: String?, description: String?, superTask: String?, adviseDate: Long?,
    ): CreateValidTaskUseCase.Response = withWriteTaskContext context@ {
        createValidTaskUseCase(
            title, type, description, superTask, adviseDate,
        ).also { response ->
            if (response !is ValidTask) return@also

            taskRepository.saveNewTask(response.task)
            return@context SavedTask(response)
        }
    }
    /**This function ignores the subtasks of the task model.*/
    suspend operator fun invoke(task: TaskModel) = invoke(
        title = task.title, type = task.type, description = task.description,
        superTask = task.superTaskTitle, adviseDate = task.adviseDate
    )

    class SavedTask(validTaskResponse: ValidTask): ValidTask(validTaskResponse.task)
}

typealias SavedTask = SaveNewTaskUseCase.SavedTask

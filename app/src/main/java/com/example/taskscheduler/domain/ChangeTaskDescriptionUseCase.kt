package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.util.ifTrue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeTaskDescriptionUseCase  @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
    private val adviseDateNotification: AdviseDateNotificationUseCase,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: String) = withWriteTaskContext context@ {
        val validDescription = createValidTaskUseCase.run {
            newValue.formatDescription()
        } //?: return@context false

        taskRepository.changeTaskDescription(task, validDescription).ifTrue {
            val addedTask = if (task is TaskModel) {
                adviseDateNotification.set(task)
                task
            } else taskRepository.getTaskByTitleStatic(task)

            adviseDateNotification.set(addedTask)
        }
    }
}

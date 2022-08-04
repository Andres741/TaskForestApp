package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.util.ifTrue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeAdviseDateUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
    private val adviseDateNotification: AdviseDateNotificationUseCase,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: Long?) = withWriteTaskContext context@{
        val validNewValue = createValidTaskUseCase.run {
            newValue?.formatAdviseTimeDate()
        }
        taskRepository.changeAdviseDate(task.taskTitle, validNewValue).ifTrue {
            val updatedTask = taskRepository.getTaskByTitleStatic(task)
            adviseDateNotification.set(updatedTask)
        }
    }
}

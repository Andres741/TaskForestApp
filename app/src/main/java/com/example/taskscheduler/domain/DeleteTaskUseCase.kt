package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.util.ifTrue
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val withWriteTaskContext: WithWriteTaskContext,
    private val adviseDateNotification: AdviseDateNotificationUseCase,
) {
    suspend operator fun invoke(taskTitle: ITaskTitleOwner) = withWriteTaskContext {
        taskRepository.deleteSingleTask(taskTitle).ifTrue {
            adviseDateNotification.delete(taskTitle)
        }
    }

    suspend fun alsoChildren(taskTitle: ITaskTitleOwner) = withWriteTaskContext {
        taskRepository.deleteTaskAndAllChildrenGettingDeleted(taskTitle)?.onEach { deletedTaskTitle ->
            adviseDateNotification.delete(deletedTaskTitle)
        }
    }
}

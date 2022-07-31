package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTitleUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
    private val adviseDateNotification: AdviseDateNotificationUseCase,
) {
    suspend operator fun invoke(
        task: ITaskTitleOwner, newValue: String
    ) = withWriteTaskContext context@ {
        val validTitle = createValidTaskUseCase.run {
            newValue.validateTitle()
        } ?: return@context null

        val isSaved = taskRepository.changeTaskTitle(task, validTitle)
        if (isSaved) {
            adviseDateNotification.delete(task)
            taskRepository.getTaskByTitleStatic(validTitle).apply(adviseDateNotification::set)
        } else null
    }
}

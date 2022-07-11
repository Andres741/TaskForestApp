package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTitleUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val saveTaskContext: SaveTaskContext,
) {
    suspend operator fun invoke(
        task: ITaskTitleOwner, newValue: String
    ) = withContext(saveTaskContext) {
        val validTitle = createValidTaskUseCase.run {
            newValue.validateTitle()
        } ?: return@withContext null

        val isSaved = taskRepository.changeTaskTitle(task, validTitle)
        if (isSaved) SimpleTaskTitleOwner(newValue) else null
    }

    suspend fun withUpdated(task: ITaskTitleOwner, newValue: String) = withContext(saveTaskContext) {
        invoke(task, newValue)?.let {
            taskRepository.getTaskByTitle(it.taskTitle)
        }
    }
}

package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {
    suspend operator fun invoke(
        task: ITaskTitleOwner, newValue: String
    ) = withContext(Dispatchers.Default) {
        val validTitle = createValidTaskUseCase.run {
            newValue.validateTitle()
        } ?: return@withContext null

        val isSaved = taskRepository.local.changeTaskTitle(task, validTitle)
        if (isSaved) SimpleTaskTitleOwner(newValue) else null
    }

    suspend fun withUpdated(task: ITaskTitleOwner, newValue: String) = withContext(Dispatchers.Default) {
        invoke(task, newValue)?.let {
            taskRepository.local.getTaskByTitle(it.taskTitle)
        }
    }
}

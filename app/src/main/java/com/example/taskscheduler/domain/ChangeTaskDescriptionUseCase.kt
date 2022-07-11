package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskDescriptionUseCase  @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val saveTaskContext: SaveTaskContext,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: String) = withContext(saveTaskContext) {
        val validDescription = createValidTaskUseCase.run {
            newValue.validateDescription()
        } ?: return@withContext false

        taskRepository.changeTaskDescription(task, validDescription).ifTrue {
            if (task is TaskModel) {
                task.description = validDescription
            }
        }
    }
}
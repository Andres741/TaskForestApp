package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.domain.models.SimpleTaskTypeNameOwner
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTypeUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val saveTaskContext: SaveTaskContext,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: String) = withContext(saveTaskContext) {
        val validType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@withContext null
        taskRepository.changeTypeInTaskHierarchy(task.taskTitle, validType).ifTrue {
            return@withContext SimpleTaskTypeNameOwner(newValue)
        }
        null
    }

    suspend operator fun invoke(oldValue: ITaskTypeNameOwner, newValue: String) = withContext(saveTaskContext) {
        val validNewType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@withContext null
        taskRepository.changeType(validNewType, oldValue.typeName).ifTrue {
            return@withContext SimpleTaskTypeNameOwner(newValue)
        }
        null
    }
}

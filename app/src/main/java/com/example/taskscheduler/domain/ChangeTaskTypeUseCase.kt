package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.domain.models.SimpleTaskTypeNameOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.domain.synchronization.WriteTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTypeUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: String) = withWriteTaskContext context@ {
        val validType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@context null
        taskRepository.changeTypeInTaskHierarchy(task.taskTitle, validType).ifTrue {
            return@context SimpleTaskTypeNameOwner(newValue)
        }
        null
    }

    suspend operator fun invoke(oldValue: ITaskTypeNameOwner, newValue: String) = withWriteTaskContext context@ {
        val validNewType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@context null
        taskRepository.changeType(validNewType, oldValue.typeName).ifTrue {
            return@context SimpleTaskTypeNameOwner(newValue)
        }
        null
    }
}

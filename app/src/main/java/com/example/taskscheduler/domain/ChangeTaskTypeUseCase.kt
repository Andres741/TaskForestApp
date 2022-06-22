package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTaskTypeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: String): Boolean = withContext(Dispatchers.Default) {
        val validType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@withContext false
        taskRepository.local.changeTypeInTaskHierarchy(task.taskTitle, validType)
    }

    suspend operator fun invoke(oldValue: ITaskTypeNameOwner, newValue: String) = withContext(Dispatchers.Default) {
        val validType = createValidTaskUseCase.run {
            newValue.validateType()
        } ?: return@withContext false
        taskRepository.local.changeType(oldValue.typeName, validType)
    }
}

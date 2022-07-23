package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeAdviseDateUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
) {
    suspend operator fun invoke(task: ITaskTitleOwner, newValue: Long?) = withWriteTaskContext context@{
        val validNewValue = createValidTaskUseCase.run {
            newValue?.apply {
                validateDate() ?: return@context false
            }
        }
        taskRepository.changeAdviseDate(task.taskTitle, validNewValue)
    }
}

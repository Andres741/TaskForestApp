package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.domain.synchronization.WriteTaskContext
import com.example.taskscheduler.domain.synchronization.WriteTaskExclusion
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val withWriteTaskContext: WithWriteTaskContext,
) {
    suspend operator fun invoke(taskTitle: ITaskTitleOwner) = withWriteTaskContext {
        taskRepository.deleteSingleTask(taskTitle)
    }

    suspend fun alsoChildren(taskTitle: ITaskTitleOwner) = withWriteTaskContext {
        taskRepository.deleteTaskAndAllChildren(taskTitle)
    }
}

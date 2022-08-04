package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import javax.inject.Inject

class GetTaskByTitleUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke(title: ITaskTitleOwner) = taskRepository.getTaskByTitle(title)

    suspend fun static(title: ITaskTitleOwner) = taskRepository.getTaskByTitleStatic(title)
}

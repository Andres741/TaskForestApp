package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import javax.inject.Inject

class GetTaskByTitleUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke(title: String) = taskRepository.getTaskByTitle(title)

    suspend fun static(title: String) = taskRepository.getTaskByTitleStatic(title)
}

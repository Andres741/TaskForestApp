package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import javax.inject.Inject

class GetTaskByNameUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(title: String) = taskRepository.local.getTaskByTitle(title)

    suspend fun static(title: String) = taskRepository.local.getTaskByTitleStatic(title)
}

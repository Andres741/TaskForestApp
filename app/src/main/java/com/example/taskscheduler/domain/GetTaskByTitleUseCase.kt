package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import javax.inject.Inject

class GetTaskByTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(title: String) = taskRepository.getTaskByTitle(title)

    suspend fun static(title: String) = taskRepository.getTaskByTitleStatic(title)
}

package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import javax.inject.Inject

class GetSuperTasksUseCase  @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke() = taskRepository.local.getSuperTopTasks()
}
package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExistsTaskWithTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(title: String): Boolean = taskRepository.local.existsTitle(title)

    suspend fun not(title: String) = invoke(title).not()
}


package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.SimpleTaskTypeNameOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExistsTaskWithTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(title: String): Boolean = taskRepository.existsTitle(title)

    suspend fun not(title: String) = invoke(title).not()

    suspend fun newTaskTitle(title: String) =
        if (invoke(title)) SimpleTaskTitleOwner(title)
        else null

}


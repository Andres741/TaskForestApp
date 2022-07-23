package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTypeNameOwner
import javax.inject.Inject

class ExistsTaskWithTypeUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    suspend operator fun invoke(type: String): Boolean = taskRepository.existsType(type)

    suspend fun not(type: String) = invoke(type).not()

    suspend fun newTaskType(type: String) =
        if (invoke(type)) SimpleTaskTypeNameOwner(type)
        else null
}


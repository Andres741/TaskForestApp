package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import javax.inject.Inject

class GetTaskPagerByTypeUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {

    operator fun invoke(type: ITaskTypeNameOwner) = taskRepository.getTaskPagingSourceByTaskType(type)
}
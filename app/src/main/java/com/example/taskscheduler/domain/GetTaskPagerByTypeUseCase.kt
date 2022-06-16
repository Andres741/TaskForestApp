package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import javax.inject.Inject

class GetTaskPagerByTypeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(type: ITaskTypeNameOwner) = taskRepository
        .local.getTaskPagingSourceByTaskType(type)
}
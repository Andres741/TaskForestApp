package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.TaskTypeModel
import javax.inject.Inject

class GetTaskPagerByTypeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(type: TaskTypeModel) = taskRepository
        .local.getTaskPagingSourceByTaskType(type)
}
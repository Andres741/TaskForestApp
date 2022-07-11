package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import javax.inject.Inject

class GetAllChildrenOfTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke(taskTitle: ITaskTitleOwner) = taskRepository.getAllChildrenPagingSource(taskTitle)
}

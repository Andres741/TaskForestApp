package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import javax.inject.Inject

class GetAllChildrenOfTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(taskTitle: ITaskTitleOwner) = taskRepository.getAllChildrenPagingSource(taskTitle)
}

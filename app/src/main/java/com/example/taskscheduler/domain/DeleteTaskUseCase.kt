package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskTitle: ITaskTitleOwner) =
        taskRepository.deleteSingleTask(taskTitle)

    suspend fun alsoChildren(taskTitle: ITaskTitleOwner) =
        taskRepository.deleteTaskAndAllChildren(taskTitle)
}

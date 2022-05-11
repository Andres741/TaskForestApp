package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeDoneStatusOfTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: TaskModel) = taskRepository.local.changeDone(task, task.isDone.not())
}

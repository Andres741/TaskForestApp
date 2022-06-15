package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTaskPagerUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(superTask: TaskModel? = null) =
        if (superTask == null) taskRepository.local.getTaskPagingSource()
        else taskRepository.local.getTaskPagingSourceBySuperTask(superTask)
}

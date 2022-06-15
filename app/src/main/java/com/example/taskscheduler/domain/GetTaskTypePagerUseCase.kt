package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.util.TaskTypeDataFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTaskTypePagerUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(): TaskTypeDataFlow = taskRepository.local.getTaskTypePagingSource()
}

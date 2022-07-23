package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.util.TaskTypeDataFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTaskTypePagerUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke(): TaskTypeDataFlow = taskRepository.getTaskTypePagingSource()
}

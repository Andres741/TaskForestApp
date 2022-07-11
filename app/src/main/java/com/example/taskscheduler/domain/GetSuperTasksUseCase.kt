package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import javax.inject.Inject

class GetSuperTasksUseCase  @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke() = taskRepository.getTopSuperTasksPagingSource()
}
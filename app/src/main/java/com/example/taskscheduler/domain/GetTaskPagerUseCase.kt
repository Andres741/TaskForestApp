package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTaskPagerUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    operator fun invoke(superTask: ITaskTitleOwner? = null) =
        if (superTask == null) taskRepository.getTaskPagingSource()
        else taskRepository.getTaskPagingSourceBySuperTask(superTask)
}

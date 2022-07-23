package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTaskTypeFromTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
) {
    suspend operator fun invoke(task: TaskModel) = withContext(Dispatchers.Default){
        taskRepository.getTaskTypeFromTask(task)
    }
}

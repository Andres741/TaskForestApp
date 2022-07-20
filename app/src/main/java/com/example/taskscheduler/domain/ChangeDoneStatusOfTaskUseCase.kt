package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.WithWriteTaskContext
import com.example.taskscheduler.domain.synchronization.WriteTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChangeDoneStatusOfTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val withWriteTaskContext: WithWriteTaskContext,
) {
    suspend operator fun invoke(task: TaskModel): Boolean = withWriteTaskContext {
        taskRepository.changeDone(task, task.isDone.not()).ifTrue {
            task.apply { isDone = !isDone }
        }
    }

    suspend operator fun invoke(
        taskTitle: SimpleTaskTitleOwner
    ): TaskModel = withContext(Dispatchers.Default) {
        val task = getTaskByTitle.static(taskTitle.taskTitle)
        invoke(task)
        task
    }
}

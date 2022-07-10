package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.synchronization.SaveTaskContext
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChangeDoneStatusOfTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val saveTaskContext: SaveTaskContext,
) {
    suspend operator fun invoke(task: TaskModel): Boolean = withContext(saveTaskContext) {
        taskRepository.changeDone(task, task.isDone.not()).ifTrue {
            task.apply { isDone = !isDone }
        }
    }

    suspend operator fun invoke(
        taskTitle: SimpleTaskTitleOwner
    ): TaskModel = withContext(saveTaskContext) {
        val task = getTaskByTitle.static(taskTitle.taskTitle)
        invoke(task)
        task
    }
}

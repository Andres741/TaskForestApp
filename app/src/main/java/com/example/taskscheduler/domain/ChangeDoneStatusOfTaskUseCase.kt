package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.ifTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger


@Singleton
class ChangeDoneStatusOfTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getTaskByTitle: GetTaskByTitleUseCase
) {
    private val mutex: Mutex = Mutex()

    suspend operator fun invoke(task: TaskModel): Boolean = mutex.withLock {

        withContext(Dispatchers.Default + NonCancellable) {
            taskRepository.local.changeDone(task, task.isDone.not()).ifTrue {
                task.apply { isDone = !isDone }
            }
        }
    }

    suspend operator fun invoke(taskTitle: SimpleTaskTitleOwner): TaskModel = withContext (
        Dispatchers.Default + NonCancellable
    ) {

        val task = getTaskByTitle.static(taskTitle.taskTitle)
        invoke(task)
        task
    }
}

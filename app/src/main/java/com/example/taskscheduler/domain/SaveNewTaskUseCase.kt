package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveNewTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {
    suspend operator fun invoke(title: String?, type: String?, description: String?, superTask: String?): Boolean {
        createValidTaskUseCase(title, type, description, superTask)?.also { newTask ->
            taskRepository.local.saveNewTask(newTask)
            return true
        }
        return false
    }
}


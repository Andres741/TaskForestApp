package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateValidTaskUseCase @Inject constructor(
    private val alreadyExistsTaskUseCase: AlreadyExistsTaskUseCase
) {
    /** If is not possible to create a valid task returns null. */
    suspend operator fun invoke(title: String?, type: String?, description: String?, superTask: String?): TaskModel? {

        val newType = type?.validateField() ?: return null
        val newTitle = title?.validateName() ?: return null
        val newSuperTask = superTask?.validateSuperTask() ?: return null
        val newDescription = description?.validateField() ?: ""

        return TaskModel(title = newTitle, type = newType, description = newDescription, superTask = newSuperTask)
    }

    private fun String.validateField(): String? {
        return ifBlank {
            null
        }
    }
    private suspend fun String.validateName(): String? {
        return validateField()?.let { newTitle ->
            if(alreadyExistsTaskUseCase(newTitle)) null else this
        }
    }
    private suspend fun String.validateSuperTask(): String? {
        return validateField()?.let { newSuperTask ->
            if(alreadyExistsTaskUseCase(newSuperTask)) return this else return null
        } ?: ""
    }
}

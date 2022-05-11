package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateValidTaskUseCase @Inject constructor(
    private val existsTaskWithTitleUseCase: ExistsTaskWithTitleUseCase
) {
    /** If is not possible to create a valid task returns null. */
    suspend operator fun invoke(
        title: String?, type: String?, description: String?, superTask: String?
    ): TaskModel? = withContext(Dispatchers.Default) {

        val newType = type?.validateField() ?: return@withContext null
        val newTitle = title?.validateName() ?: return@withContext null
        val newSuperTask = superTask?.validateSuperTask() ?: return@withContext null
        val newDescription = description?.validateField() ?: ""

        return@withContext TaskModel(
            title = newTitle, type = newType, description = newDescription, superTask = newSuperTask
        )
    }

    private fun String.validateField(): String? {
        return ifBlank {
            null
        }
    }
    private suspend fun String.validateName(): String? {
        return validateField()?.let { newTitle ->
            if(existsTaskWithTitleUseCase(newTitle).not()) this else null
        }
    }
    private suspend fun String.validateSuperTask(): String? {
        return validateField()?.let { newSuperTask ->
            if(existsTaskWithTitleUseCase(newSuperTask)) this else return null
        } ?: ""
    }
}

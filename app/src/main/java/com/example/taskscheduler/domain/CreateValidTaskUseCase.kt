package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateValidTaskUseCase @Inject constructor(
    private val existsTaskWithTitleUseCase: ExistsTaskWithTitleUseCase,
    private val taskRepository: TaskRepository,
) {
    /**
     * Returns a Successful instance with a valid TaskModel ready to be saved in the database or other
     * subtype of Response class is one of the arguments is not valid to create a new TaskModel considering
     * the data in the database.
     * If there are super task the type will be the same as the super task, ignoring type param
     */
    suspend operator fun invoke(
        title: String?, type: String? = null, description: String?, superTask: String? = null
    ): Response = withContext(Dispatchers.Default) res@ {

        val (newSuperTask, newType) = if (superTask.isNullOrBlank()) { // There is not super task
            "" to (type?.validateField() ?: return@res Response.WrongType)
        } else {
            (superTask.validateSuperTask() ?: return@res Response.WrongSuperTask) to taskRepository.local.getTaskTypeByTitleStatic(superTask)
        }

        val newTitle = title?.validateName() ?: return@res Response.WrongTitle
        val newDescription = description?.validateField() ?: ""

        return@res Response.Successful( TaskModel (
            title = newTitle, type = newType, description = newDescription, superTask = newSuperTask
        ))
    }

    private fun String.validateField(): String? = ifBlank { null }

    private suspend fun String.validateName(): String? = validateField()?.let { newTitle ->
        if(existsTaskWithTitleUseCase.not(newTitle)) this else null
    }

    private suspend fun String.validateSuperTask(): String? = validateField()?.let { newSuperTask ->
            if(existsTaskWithTitleUseCase(newSuperTask)) this else return null
    } ?: ""

    sealed class Response {
        open class Successful(val task: TaskModel): Response()
        object WrongTitle: Response()
        object WrongType: Response()
        object WrongSuperTask: Response()
    }
}

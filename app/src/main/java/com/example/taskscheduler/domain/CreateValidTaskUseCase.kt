package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateValidTaskUseCase @Inject constructor(
    private val existsTaskWithTitleUseCase: ExistsTaskWithTitleUseCase,
    private val taskRepository: ITaskRepository,
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
            "" to (type?.validateType() ?: return@res Response.WrongType)
        } else {
            (superTask.validateSuperTask() ?: return@res Response.WrongSuperTask) to taskRepository.getTaskTypeByTitleStatic(superTask)
        }

        val newTitle = title?.validateTitle() ?: return@res Response.WrongTitle
        val newDescription = description?.validateDescription() ?: ""

        return@res Response.ValidTask( TaskModel (
            title = newTitle, type = newType, description = newDescription, superTask = SimpleTaskTitleOwner(newSuperTask)
        ))
    }

    private inline fun String.validateField(): String? = ifBlank { null }
    private inline fun String.validateShortField(): String? = validateField()?.takeIf { field ->
        field.length < 35
    }


    suspend fun String.validateTitle(): String? = validateShortField()?.let { newTitle ->
        if(existsTaskWithTitleUseCase(newTitle)) null else this
    }

    fun String.validateType() = validateShortField()

    private suspend fun String.validateSuperTask(): String? = validateShortField()?.let { newSuperTask ->
            if(existsTaskWithTitleUseCase(newSuperTask)) this else return null
    } ?: ""

    fun String.validateDescription(): String? {
        ifBlank {
            return ""
        }
        return validateField()
    }

    sealed class Response {
        open class ValidTask(val task: TaskModel): Response()
        object WrongTitle: Response()
        object WrongType: Response()
        object WrongSuperTask: Response()
    }
}

typealias ValidTask = CreateValidTaskUseCase.Response.ValidTask
typealias WrongTitle = CreateValidTaskUseCase.Response.WrongTitle
typealias WrongType = CreateValidTaskUseCase.Response.WrongType
typealias WrongSuperTask = CreateValidTaskUseCase.Response.WrongSuperTask

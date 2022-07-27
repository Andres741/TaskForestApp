package com.example.taskscheduler.domain

import android.util.Log
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
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
        title: String?, type: String? = null, description: String?,
        superTask: String? = null, adviseDate: Long?,
    ): Response = withContext(Dispatchers.Default) res@ {

        val newTitle = title?.validateTitle() ?: return@res Response.WrongTitle

        val (newSuperTask, newType) = if (superTask.isNullOrBlank()) { // There is not super task
            "" to (type?.validateType() ?: return@res Response.WrongType)
        } else {
            (superTask.validateSuperTask() ?: return@res Response.WrongSuperTask) to taskRepository.getTaskTypeByTitleStatic(superTask)
        }

        adviseDate?.apply {
            formatDate() ?: return@res WrongAdviseDate
        }

        val newDescription = description?.validateDescription() ?: ""

        return@res Response.ValidTask( TaskModel (
            title = newTitle, type = newType, description = newDescription,
            superTask = SimpleTaskTitleOwner(newSuperTask), adviseDate = adviseDate
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

    fun isDateValid(millis: Long?, nowMillis: Long? = null): Boolean {
        if (millis == null) return true

        val nowsDate = Calendar.getInstance().apply {
            timeInMillis = nowMillis ?: return@apply
        }
        val date = Calendar.getInstance().apply {
            timeInMillis = millis
        }

        val nowYear = nowsDate.get(Calendar.YEAR).log("\nnow year")
        val year = date.get(Calendar.YEAR).log("year")

        val nowMonth = nowsDate.get(Calendar.MONTH).log("\nnow month")
        val month = date.get(Calendar.MONTH).log("month")

        val nowDay = nowsDate.get(Calendar.DAY_OF_MONTH).log("\nnow day")
        val day = date.get(Calendar.DAY_OF_MONTH).log("day")

        return nowYear < year || nowYear == year &&
                nowMonth < month || nowMonth == month &&
                nowDay < day
    }

    fun Long.formatDate(): Long? {
        return if (isDateValid(this, System.currentTimeMillis())) {
            "--Is the future--".log()
            val date = Calendar.getInstance().apply {
                timeInMillis = this@formatDate
            }

            val year = date.get(Calendar.YEAR).log("year")
            val month = date.get(Calendar.MONTH).log("month")
            val day = date.get(Calendar.DAY_OF_MONTH).log("day")

            GregorianCalendar(year, month, day,22,0,0).timeInMillis
        } else {
            "--Is not the future--".log()
            null
        }
    }

    sealed class Response {
        open class ValidTask(val task: TaskModel): Response()
        object WrongTitle: Response()
        object WrongType: Response()
        object WrongAdviseDate: Response()
        object WrongSuperTask: Response()
    }
    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("CreateValidTaskUseCase", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

typealias ValidTask = CreateValidTaskUseCase.Response.ValidTask
typealias WrongTitle = CreateValidTaskUseCase.Response.WrongTitle
typealias WrongType = CreateValidTaskUseCase.Response.WrongType
typealias WrongAdviseDate = CreateValidTaskUseCase.Response.WrongAdviseDate
typealias WrongSuperTask = CreateValidTaskUseCase.Response.WrongSuperTask

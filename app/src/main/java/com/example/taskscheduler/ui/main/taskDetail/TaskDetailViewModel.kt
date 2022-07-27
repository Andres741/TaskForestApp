package com.example.taskscheduler.ui.main.taskDetail

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.di.util.AppDateAndHourFormatProvider
import com.example.taskscheduler.di.util.AppDateFormatProvider
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.ifTrue
import com.example.taskscheduler.util.observable.DataEventTrigger
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.NoMoreWithTaskDeletedType
import com.example.taskscheduler.util.TypeChange
import com.example.taskscheduler.util.ifNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val changeTitle: ChangeTaskTitleUseCase,
    private val changeType: ChangeTaskTypeUseCase,
    private val changeDescription: ChangeTaskDescriptionUseCase,
    private val changeAdviseDate: ChangeAdviseDateUseCase,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val existsTaskWithType: ExistsTaskWithTypeUseCase,
    dateAndHourFormatProvider: AppDateAndHourFormatProvider,
    dateFormatProvider: AppDateFormatProvider,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val adviseDate = MutableLiveData<Long?>()

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskTitleChangedEvent = MutableLiveData<ITaskTitleOwner>()
    val taskTitleChangedEvent: LiveData<ITaskTitleOwner> = _taskTitleChangedEvent

    private val _typeChangedEvent = MutableLiveData<TypeChange>()
    val typeChangedEvent: LiveData<TypeChange> = _typeChangedEvent

    val taskDeletedEvent = DataEventTrigger<NoMoreWithTaskDeletedType>()

    private val scopeProvider = OneScopeAtOnceProvider()

    val creationDateFormat: SimpleDateFormat = dateAndHourFormatProvider.value
    val adviseDateFormat: SimpleDateFormat = dateFormatProvider.value

    val formattedCreationDate = _task.map { task ->
        task ?: return@map ""
        creationDateFormat.format(task.dateNum)
    }
    val formattedAdviseDate = adviseDate.map { numDate ->
        numDate ?: return@map null
        adviseDateFormat.format(numDate)
    }

    fun onSetUp(task: ITaskTitleOwner) {
        scopeProvider.newScope.launch {
            try {
                getTaskByTitle(task.taskTitle).collectLatest { latestTask ->
                    _task.value = latestTask
                    title.value = latestTask.title
                    type.value = latestTask.type
                    description.value = latestTask.description
                    adviseDate.value = latestTask.adviseDate
                }
            } catch (e: Exception) {
                e.log("Exception collecting tasks")
            }
        }
    }

    fun saveNewTitle() {
        val newTitle = title.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeTitle(task, newTitle)?.also { newTitle ->
                scopeProvider.cancel()
                _taskTitleChangedEvent.value = newTitle
            }
        }
    }

    fun saveNewTypeInTaskHierarchy() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTitleOwner(), newType)?.also { newType ->
                _typeChangedEvent.value = task.toSimpleTaskTypeNameOwner() to newType
            }
        }
    }

    fun saveNewType() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTypeNameOwner(), newType)?.also{ newType ->
                _typeChangedEvent.value = task.toSimpleTaskTypeNameOwner() to newType
            }
        }
    }

    fun saveNewDescription() {
        val newDescription = description.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeDescription(task, newDescription)
        }
    }

    fun saveNewAdviseDate() {
        val task = _task.value ?: return
        val newAdviseDate = adviseDate.value

        viewModelScope.launch {
            changeAdviseDate(task, newAdviseDate)
        }
    }

    fun restoreTitle() {
        title.value = _task.value!!.title
    }
    fun restoreType() {
        type.value = _task.value!!.type
    }
    fun restoreDescription() {
        description.value = _task.value!!.description
    }
    fun restoreAdviseDate() {
        adviseDate.value = _task.value?.adviseDate
    }

    fun deleteOnlyTopStackTask() {
        val task = _task.value ?: return
        viewModelScope.launch {
            deleteTask(task).ifTrue {
                scopeProvider.cancel()
                taskDeletedEvent.triggerEvent(existsTaskWithType(task.type))
            }
        }
    }

    fun deleteTopStackTaskAndChildren() {
        val task = _task.value ?: return
        viewModelScope.launch {
            deleteTask.alsoChildren(task).ifNotNull {
                scopeProvider.cancel()
                taskDeletedEvent.triggerEvent(existsTaskWithType(task.type))
            }
        }
    }

    override fun onCleared() {
        scopeProvider.cancel()
        super.onCleared()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("TaskDetailViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

package com.example.taskscheduler.ui.main.taskDetail

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.ifTrue
import com.example.taskscheduler.util.observable.DataEventTrigger
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.NoMoreWithTaskDetedType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val changeTitle: ChangeTaskTitleUseCase,
    private val changeType: ChangeTaskTypeUseCase,
    private val changeDescription: ChangeTaskDescriptionUseCase,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val existsTaskWithType: ExistsTaskWithTypeUseCase,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskTitleChangedEvent = MutableLiveData<ITaskTitleOwner>()
    val taskTitleChangedEvent: LiveData<ITaskTitleOwner> = _taskTitleChangedEvent

    private val _typeChangedEvent = MutableLiveData<ITaskTypeNameOwner>()
    val typeChangedEvent: LiveData<ITaskTypeNameOwner> = _typeChangedEvent

    val taskDeletedEvent = DataEventTrigger<NoMoreWithTaskDetedType>()

    private val scopeProvider = OneScopeAtOnceProvider()

    fun onSetUp(task: ITaskTitleOwner) {
        scopeProvider.newScope.launch {
            try {
                getTaskByTitle(task.taskTitle).collectLatest { latestTask ->
                    _task.value = latestTask
                    title.value = latestTask.title
                    type.value = latestTask.type
                    description.value = latestTask.description
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
            changeType(task.toSimpleTaskTitleOwner(), newType).also { newType ->
                newType ?: return@also
                _typeChangedEvent.value = newType
            }
        }
    }

    fun saveNewType() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTypeNameOwner(), newType).also { newType ->
                newType ?: return@also
                _typeChangedEvent.value = newType
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

    fun restoreTitle() {
        title.value = _task.value!!.title
    }
    fun restoreType() {
        type.value = _task.value!!.type
    }
    fun restoreDescription() {
        description.value = _task.value!!.description
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
            deleteTask.alsoChildren(task).ifTrue {
                scopeProvider.cancel()
                taskDeletedEvent.triggerEvent(existsTaskWithType(task.type))
            }
        }
    }

    override fun onCleared() {
        scopeProvider.cancel()
        super.onCleared()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("TaskDetailViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

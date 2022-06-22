package com.example.taskscheduler.ui.main.taskDetail

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.domain.ChangeTaskDescriptionUseCase
import com.example.taskscheduler.domain.ChangeTaskTitleUseCase
import com.example.taskscheduler.domain.ChangeTaskTypeUseCase
import com.example.taskscheduler.domain.GetTaskByTitleUseCase
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val changeTitle: ChangeTaskTitleUseCase,
    private val changeType: ChangeTaskTypeUseCase,
    private val changeDescription: ChangeTaskDescriptionUseCase,
    private val getTaskByTitle: GetTaskByTitleUseCase
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskTitleChangedEvent = MutableLiveData<ITaskTitleOwner>()
    val taskTitleChangedEvent: LiveData<ITaskTitleOwner> = _taskTitleChangedEvent

    private val scopeProvider = OneScopeAtOnceProvider()

    fun onSetUp(taskTitle: ITaskTitleOwner) {
        scopeProvider.newScope.launch {
            getTaskByTitle(taskTitle.taskTitle).collectLatest { latestTask ->
                try {
                    _task.value = latestTask
                    title.value = latestTask.title
                    type.value = latestTask.type
                    description.value = latestTask.description
                } catch (t: Throwable) {
                    "Exception in getTaskByTitle(taskTitle.taskTitle).collectLatest".log()
                }
            }
        }
    }

    fun saveNewTitle() {
        val newTitle = title.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeTitle(task, newTitle).also { newTitle ->
                scopeProvider.cancel()
                _taskTitleChangedEvent.value = newTitle
            }
        }
    }

    fun saveNewTypeInTaskHierarchy() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTitleOwner(), newType)
        }
    }

    fun saveNewType() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTypeNameOwner(), newType)
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

    override fun onCleared() {
        scopeProvider.cancel()
        super.onCleared()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("TaskDetailViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

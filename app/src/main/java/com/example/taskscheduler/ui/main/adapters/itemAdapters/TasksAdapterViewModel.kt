package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerByTypeUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.GetTaskTypeFromTaskUseCase
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.util.TaskDataFlow
import com.example.taskscheduler.util.observable.EventTrigger
import com.example.taskscheduler.util.observable.LiveStack
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class TasksAdapterViewModel @Inject constructor(
    private val getTaskPagerUseCase: GetTaskPagerUseCase,
    private val getTaskPagerByTypeUseCase: GetTaskPagerByTypeUseCase,
    private val changeDoneStatusOfTaskUseCase: ChangeDoneStatusOfTaskUseCase,
    private val getTaskTypeFromTaskUseCase: GetTaskTypeFromTaskUseCase,
): ViewModel() {
    private val _taskTitleStack = LiveStack<ITaskTitleOwner>()
    /**
     * If this stack is empty TasksFragment must be shown, else TaskDetailFragment must show
     * the top task of this stack.
     */
    val taskTitleStack: LiveData<ITaskTitleOwner> = _taskTitleStack

    private val pagingDataScopeProvider = OneScopeAtOnceProvider()

    private val _tasksDataFlow = MutableLiveData<TaskDataFlow>().apply {
        value = getTaskPagerUseCase().cachedIn(pagingDataScopeProvider.newScope)
    }
    val tasksDataFlow: LiveData<TaskDataFlow> = _tasksDataFlow

    val selectedTaskTypeName = MutableLiveData<ITaskTypeNameOwner?>()

    val onUpButtonPressedEvent = EventTrigger()


    private fun setPagingData(newTask: ITaskTitleOwner?) {
        _tasksDataFlow.value = getTaskPagerUseCase(newTask).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromType(type: ITaskTypeNameOwner) {
        _tasksDataFlow.value = getTaskPagerByTypeUseCase(type).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromTopOfStack() = setPagingData(_taskTitleStack.value)


    fun addToStack(taskTitle: ITaskTitleOwner) {
        _taskTitleStack.add(taskTitle)
        setPagingData(taskTitle)
    }

    fun removeFromStack() {
        _taskTitleStack.remove()
        setPagingDataFromTopOfStack()
    }

    fun changeStackTop(taskTitle: ITaskTitleOwner) {
        _taskTitleStack.changeTop(taskTitle)
        setPagingData(taskTitle)
    }

    fun removeFromStackIfMoreThanOneElement() {
        if (_taskTitleStack.size > 1) {
            removeFromStack()
        }
    }

    fun popStack(): ITaskTitleOwner? {
        return _taskTitleStack.pop().also { setPagingDataFromTopOfStack() }
    }

    fun clearStack() {
        _taskTitleStack.clear()
        setPagingData(null)
    }

    fun filterByType(typeName: ITaskTypeNameOwner?) {
        if (_taskTitleStack.isNotEmpty()) {
            "Impossible to filter by type if the _taskStack is not empty".log()
            return
        }
        if (typeName == null) {
            setPagingData(null)  // setPagingData(type) // Ugly but works!
            return
        }
        setPagingDataFromType(typeName)
    }

    suspend fun changeDoneStatusOf(task: TaskModel) = changeDoneStatusOfTaskUseCase(task)//.log("Status changed")

    fun changeDoneStatusOfTopTask() {
        viewModelScope.launch {
            val topTask = _taskTitleStack.value ?: return@launch
            changeDoneStatusOfTaskUseCase(topTask.toSimpleTaskTitleOwner())
        }
    }

    suspend fun getTaskTypeFromTask(task: TaskModel) = getTaskTypeFromTaskUseCase(task)

    override fun onCleared() {
        pagingDataScopeProvider.cancel()
        super.onCleared()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i(
            "TasksAdapterViewModel",
            "${if (msj != null) "$msj: " else ""}${toString()}"
        )
    }

//    sealed class SelectedTaskType(
//        val taskType: TaskTypeModel
//    ) {
//        class FromTaskTypeItem(taskType: TaskTypeModel): SelectedTaskType(taskType)
//        class FromAnywhere(taskType: TaskTypeModel): SelectedTaskType(taskType)
//    }
}

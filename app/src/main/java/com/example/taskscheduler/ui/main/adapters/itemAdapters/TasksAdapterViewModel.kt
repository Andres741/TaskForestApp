package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerByTypeUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.GetTaskTypeFromTaskUseCase
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
    private val _taskStack = LiveStack<TaskModel>()
    /**
     * If this stack is empty TasksFragment must be shown, else TaskDetailFragment must show
     * the top task of this stack.
     */
    val taskStack: LiveData<TaskModel> = _taskStack

    private val pagingDataScopeProvider = OneScopeAtOnceProvider()

    private val _tasksDataFlow = MutableLiveData<TaskDataFlow>().apply {
        value = getTaskPagerUseCase().cachedIn(pagingDataScopeProvider.newScope)
    }
    val tasksDataFlow: LiveData<TaskDataFlow> = _tasksDataFlow

    val selectedTaskTypeName = MutableLiveData<ITaskTypeNameOwner?>()

    val onUpButtonPressedEvent = EventTrigger()


    private fun setPagingData(newTask: TaskModel?) {
        _tasksDataFlow.value = getTaskPagerUseCase(newTask).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromType(type: ITaskTypeNameOwner) {
        _tasksDataFlow.value = getTaskPagerByTypeUseCase(type).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromTopOfStack() = setPagingData(_taskStack.value)


    fun addToStack(task: TaskModel) {
        _taskStack.add(task)
        setPagingDataFromTopOfStack()
    }

    fun removeFromStack() {
        _taskStack.remove()
        setPagingDataFromTopOfStack()
    }

    fun popStack(): TaskModel? {
        return _taskStack.pop().also { setPagingDataFromTopOfStack() }
    }

    fun clearStack() {
        _taskStack.clear()
        setPagingData(null)
    }

    fun filterByType(typeName: ITaskTypeNameOwner?) {
        if (_taskStack.isNotEmpty()) {
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
            val topTask = _taskStack.value ?: return@launch

            changeDoneStatusOfTaskUseCase(topTask)
            _taskStack.notifyObserveAgain()
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

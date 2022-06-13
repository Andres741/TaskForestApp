package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.util.observable.LiveStack
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias TaskDataFlow = Flow<PagingData<TaskModel>>
typealias TaskTypeDataFlow = Flow<PagingData<TaskTypeModel>>

@HiltViewModel
class TasksAdapterViewModel @Inject constructor(
    private val getTaskPagerUseCase: GetTaskPagerUseCase,
    private val changeDoneStatusOfTaskUseCase: ChangeDoneStatusOfTaskUseCase,
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

    val taskTypeData: TaskTypeDataFlow = TODO()


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

    private fun setPagingData(newTask: TaskModel?) {
        _tasksDataFlow.value = getTaskPagerUseCase(newTask).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromTopOfStack() = setPagingData(_taskStack.value)

    suspend fun changeDoneStatusOf(task: TaskModel) = changeDoneStatusOfTaskUseCase(task)//.log("Status changed")

    fun changeDoneStatusOfTopTask() {
        viewModelScope.launch {
            val topTask = _taskStack.value ?: return@launch

            changeDoneStatusOfTaskUseCase(topTask)
            _taskStack.notifyObserveAgain()
        }
    }

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
}

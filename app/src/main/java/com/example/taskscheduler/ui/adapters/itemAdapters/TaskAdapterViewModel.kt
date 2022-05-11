package com.example.taskscheduler.ui.adapters.itemAdapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.observable.LiveStack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskAdapterViewModel @Inject constructor(
    private val getTaskPagerUseCase: GetTaskPagerUseCase,
    private val changeDoneStatusOfTaskUseCase: ChangeDoneStatusOfTaskUseCase,
): ViewModel() {

    private val _taskStack = LiveStack<TaskModel>()
    /**
     * If this stack is empty TasksFragment must be shown, else TaskDetailFragment must show
     * the top task of this stack.
     */
    val taskStack: LiveData<TaskModel> = _taskStack


    var pagingDataFlow: Flow<PagingData<TaskModel>>
        private set

    init {
        pagingDataFlow = getTaskPagerUseCase()
    }

    fun addToStack(task: TaskModel) {
        _taskStack.add(task)
        setPagingDataFromTopOfStack()
    }

    fun removeFromStack() {
        _taskStack.remove()
        setPagingDataFromTopOfStack()
    }

    fun popFromStack(): TaskModel? {
        val res = _taskStack.pop()
        setPagingDataFromTopOfStack()
        return res
    }

    private fun setPagingDataFromTopOfStack() {
        val topTask = _taskStack.value
        pagingDataFlow = getTaskPagerUseCase(topTask)
    }

    fun changeDoneStatusOf(task: TaskModel) {
        viewModelScope.launch { changeDoneStatusOfTaskUseCase(task) }
    }
}

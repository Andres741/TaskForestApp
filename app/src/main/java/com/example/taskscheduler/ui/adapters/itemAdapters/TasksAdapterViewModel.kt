package com.example.taskscheduler.ui.adapters.itemAdapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.observable.LiveStack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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


    private val _pagingDataFlow = MutableLiveData<Flow<PagingData<TaskModel>>>()
    val pagingDataFlow: LiveData<Flow<PagingData<TaskModel>>> = _pagingDataFlow
        //private set

    init {
        //Todo: crate custom scope for the method cachedIn.
        _pagingDataFlow.value = getTaskPagerUseCase().cachedIn(viewModelScope)
            .map { it.map(TaskWithSuperAndSubTasks::toModel) }
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
        _pagingDataFlow.value = getTaskPagerUseCase(topTask).cachedIn(viewModelScope).map { it.map(TaskWithSuperAndSubTasks::toModel) }
    }

    fun changeDoneStatusOf(task: TaskModel) {
        viewModelScope.launch { changeDoneStatusOfTaskUseCase(task) }
    }
}

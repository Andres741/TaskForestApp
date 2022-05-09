package com.example.taskscheduler.ui.adapters.fragmentAdapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.taskscheduler.domain.ChangeDoneStatusOfTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerBySuperTaskUseCase
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.observable.LiveStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class TaskAdapterViewModel @Inject constructor(
    private val getTaskPagerUseCase: GetTaskPagerUseCase,
    private val getTaskPagerBySuperTaskUseCase: GetTaskPagerBySuperTaskUseCase,
    private val changeDoneStatusOfTaskUseCase: ChangeDoneStatusOfTaskUseCase,
): ViewModel() {

    private val _taskStack = LiveStack<TaskModel>()
    /**
     * If this stack is empty TasksFragment must be shown, else TaskDetailFragment must show
     * the top task of this stack.
     */
    val taskStack: LiveData<TaskModel> = _taskStack


    //TODO: Decide which one I will use.
    var pagingDataFlow: Flow<PagingData<TaskModel>>
        private set
    var pagingLiveData: LiveData<PagingData<TaskModel>>
        private set

    init {
        getTaskPagerUseCase().apply {
            pagingDataFlow = flow
            pagingLiveData = liveData
        }
    }

    fun addToStack(task: TaskModel) {
        _taskStack.add(task)

    }
    fun removeFromStack() {
        _taskStack.remove()

    }
    fun popFromStack(): TaskModel? = _taskStack.pop().also {

    }

    private fun setPagingDataFromTopOfStack() {
        val topTask = _taskStack.value

        val pager = if (topTask != null) {
            getTaskPagerBySuperTaskUseCase(topTask)
        } else {
            getTaskPagerUseCase()
        }

        pager.apply {
            pagingDataFlow = flow
            pagingLiveData = liveData
        }
    }

    fun changeDoneStatusOf(task: TaskModel) {
        viewModelScope.launch { changeDoneStatusOfTaskUseCase(task) }
    }
}

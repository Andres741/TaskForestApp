package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel.Companion.defaultFilter
import com.example.taskscheduler.util.TaskDataFlow
import com.example.taskscheduler.util.and
import com.example.taskscheduler.util.observable.LiveStack
import com.example.taskscheduler.util.observeAgain
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TasksAdapterViewModel @Inject constructor(
    private val getTaskPager: GetTaskPagerUseCase,
    private val getTaskPagerByType: GetTaskPagerByTypeUseCase,
    private val changeDoneStatusOfTask: ChangeDoneStatusOfTaskUseCase,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val getSuperTasks: GetSuperTasksUseCase,
): ViewModel() {
    private val _taskTitleStack = LiveStack<ITaskTitleOwner>()
    /**
     * If this stack is empty TasksFragment must be shown, else TaskDetailFragment must show
     * the top task of this stack.
     */
    val taskTitleStack: LiveData<ITaskTitleOwner> = _taskTitleStack

    private val pagingDataScopeProvider = OneScopeAtOnceProvider()

    private val _tasksDataFlow = MutableLiveData(
        getTaskPager().cachedIn(pagingDataScopeProvider.newScope)
    )

    private var filters = Filters()

    private val filteredDataFlow = _tasksDataFlow.map { taskDataFlow ->
        taskDataFlow.map { pagingData ->
            pagingData.filter(filters::andAll)
        }
    }

    val tasksDataFlow: LiveData<TaskDataFlow> = filteredDataFlow


    private fun setPagingData(newTask: ITaskTitleOwner?) {
        _tasksDataFlow.value = getTaskPager(newTask).cachedIn(pagingDataScopeProvider.newScope)
    }

    private fun setPagingDataFromType(type: ITaskTypeNameOwner) {
        _tasksDataFlow.value = getTaskPagerByType(type).cachedIn(pagingDataScopeProvider.newScope)
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

    fun goToSuperTask() {
        if (_taskTitleStack.size > 1) {
            removeFromStack()
        } else if (_taskTitleStack.size == 1) {
            viewModelScope.launch {
                val task = getTaskByTitle.static(_taskTitleStack[0].taskTitle)
                if (! task.hasSuperTask) return@launch
                changeStackTop(task.superTask)
            }
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
        filters.typeFilter = if (typeName == null) defaultFilter
            else { task -> task equalsType typeName }
    }

    fun filterByIsDone(done: Boolean? = null) {
        filters.doneFilter = when(done) {
            true -> { task -> task.isDone }
            false -> { task -> task.isDone.not() }
            null -> defaultFilter
        }
    }

    fun allInTaskSource() {
        setPagingData(_taskTitleStack.value)
    }

    fun onlySuperTasksInTaskSource() {
        _tasksDataFlow.value = getSuperTasks()
    }

    suspend fun changeDoneStatusOf(task: TaskModel) = changeDoneStatusOfTask(task)//.log("Status changed")

    fun changeDoneStatusOfTopTask() {
        val topTask = _taskTitleStack.value ?: return
        CoroutineScope(Dispatchers.Default).launch {
            changeDoneStatusOfTask(topTask.toSimpleTaskTitleOwner())
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

    /**
     * This class makes easy put new filters.
     * The filters of this class should be able to overlap.
     */
    private inner class Filters {
        var doneFilter = defaultFilter
            set(value) {
                field = value
                _tasksDataFlow.observeAgain()
            }

        var typeFilter = defaultFilter
            set(value) {
                field = value
                _tasksDataFlow.observeAgain()
            }

//        TODO:
//        var dateFilter = defaultFilter
//            set(value) {
//                field = value
//                _tasksDataFlow.observeAgain()
//            }

        fun andAll(task: TaskModel) = doneFilter(task) && typeFilter(task) //&& dateFilter(task)
    }
    private companion object {
        val defaultFilter = { _:TaskModel -> true }
    }
}

package com.example.taskscheduler.ui.main.adapters.itemAdapters

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.*
import com.example.taskscheduler.util.TaskDataFlow
import com.example.taskscheduler.util.observable.LiveStack
import com.example.taskscheduler.util.observeAgain
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TasksAdapterViewModel @Inject constructor(
    private val getTaskPager: GetTaskPagerUseCase,
    private val changeDoneStatusOfTask: ChangeDoneStatusOfTaskUseCase,
    private val getSuperTasks: GetSuperTasksUseCase,
    private val getAllChildren: GetAllChildrenOfTaskUseCase,
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

    private var filters = TaskFilters()

    private val filteredDataFlow = _tasksDataFlow.map { taskDataFlow ->
        taskDataFlow.map { pagingData ->
            pagingData.filter(filters::andAll)
        }
    }

    val tasksDataFlow: LiveData<TaskDataFlow> = filteredDataFlow

    val selectedTaskTypeName = MutableLiveData<ITaskTypeNameOwner?>()


    private fun setPagingData(newTask: ITaskTitleOwner?) {
        _tasksDataFlow.value = getTaskPager(newTask).cachedIn(pagingDataScopeProvider.newScope)
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

    fun goToSuperTask(currentTask: TaskModel) {
        if (_taskTitleStack.size > 1) {
            val secondTaskInStack = _taskTitleStack[1]
            if (secondTaskInStack notEqualsTitle currentTask.superTask ) _taskTitleStack.setNotTop(1, currentTask.superTask)
            removeFromStack()
        } else if (_taskTitleStack.size == 1) {
            if (! currentTask.hasSuperTask) return
            changeStackTop(currentTask.superTask)
        }
        //if _taskTitleStack.size < 1 nothing
    }

    fun popStack(): ITaskTitleOwner? {
        return _taskTitleStack.pop().also { setPagingDataFromTopOfStack() }
    }

    fun clearStack() {
        _taskTitleStack.clear()
        setPagingData(null)
    }

    fun filterByType(typeName: ITaskTypeNameOwner?) {
        filters.typeFilterCriteria = typeName
    }

    fun filterByIsDone(done: Boolean?) {
        filters.doneFilterCriteria = done
    }

    fun allInTaskSource() {
        setPagingData(_taskTitleStack.value)
    }

    fun onlySuperTasksInTaskSource() {
        _tasksDataFlow.value = getSuperTasks()
    }

    fun allTopStackTaskChildren() {
        val topStackTask = _taskTitleStack.value ?: return
        _tasksDataFlow.value = getAllChildren(topStackTask)
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

    /**
     * This class makes easy put new filters.
     * The filters of this class should be able to overlap.
     */
    private inner class TaskFilters {

        var doneFilterCriteria: Boolean? = null
            set(value) {
                if (field == value) return
                field = value
                doneFilter = when(value) {
                    null -> defaultTaskFilter
                    true -> { task -> task.isDone }
                    false -> { task -> task.isDone.not() }
                }
            }

        var typeFilterCriteria: ITaskTypeNameOwner? = null
            set(value) {
                if (field == value) return
                field = value
                typeFilter = if (value == null) defaultTaskFilter
                else { task -> task equalsType value }
            }

        var doneFilter = defaultTaskFilter
            private set(value) {
                field = value
                _tasksDataFlow.observeAgain()
            }

        var typeFilter = defaultTaskFilter
            private set(value) {
                field = value
                _tasksDataFlow.observeAgain()
            }

        fun andAll(task: TaskModel) = doneFilter(task) && typeFilter(task) //&& dateFilter(task)
    }
    private companion object {
        val defaultTaskFilter = { _ :TaskModel -> true }
    }

//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i(
//            "TasksAdapterViewModel",
//            "${if (msj != null) "$msj: " else ""}${toString()}"
//        )
//    }
}

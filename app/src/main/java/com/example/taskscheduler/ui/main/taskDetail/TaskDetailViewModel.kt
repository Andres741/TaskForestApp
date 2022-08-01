package com.example.taskscheduler.ui.main.taskDetail

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.di.util.AppDateAndHourFormatProvider
import com.example.taskscheduler.di.util.AppDateFormatProvider
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.util.ifTrue
import com.example.taskscheduler.util.observable.DataEventTrigger
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.NoMoreWithTaskDeletedType
import com.example.taskscheduler.util.TypeChange
import com.example.taskscheduler.util.ifNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val changeTitle: ChangeTaskTitleUseCase,
    private val changeType: ChangeTaskTypeUseCase,
    private val changeDescription: ChangeTaskDescriptionUseCase,
    private val changeAdviseDate: ChangeAdviseDateUseCase,
    private val getTaskByTitle: GetTaskByTitleUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val existsTaskWithType: ExistsTaskWithTypeUseCase,
    private val createValidTask: CreateValidTaskUseCase,
    dateAndHourFormatProvider: AppDateAndHourFormatProvider,
    dateFormatProvider: AppDateFormatProvider,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val adviseDate = MutableLiveData<Long?>()

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task


    private val _saveTitleStatus = MutableLiveData<SavedStatus>()
    init {
        viewModelScope.launch {
            title.asFlow().collectLatest { latest -> latest!!
                _saveTitleStatus.value = if (latest == _task.value!!.title) SavedStatus.Saved
                else if (createValidTask.run { latest.validateTitle() != null }) SavedStatus.Savable
                else SavedStatus.NotSavable
            }
        }
    }
    val saveTitleStatus: LiveData<SavedStatus> = _saveTitleStatus
    val saveTypeStatus = type.map { latest -> latest!!
        if (latest == _task.value!!.type) SavedStatus.Saved
        else if (createValidTask.run { latest.validateType() != null }) SavedStatus.Savable
        else SavedStatus.NotSavable
    }
    val saveDescriptionStatus = description.map { latest -> latest!!
        if (latest == _task.value!!.description) SavedStatus.Saved
        else if (createValidTask.run { true }) SavedStatus.Savable
        else SavedStatus.NotSavable
    }
    val saveAdviseDateStatus = adviseDate.map { latest ->
        if (latest == _task.value!!.adviseDate) SavedStatus.Saved
//        else if (createValidTask.isDateValid(latest)) SavedStatus.Savable
        else SavedStatus.Savable
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    adviseDate.asFlow().collectLatest { newValue ->
                        if (newValue == null || saveAdviseDateStatus.value == SavedStatus.Saved) {
                            updateMinuteScopeProvider.cancel()
                            return@collectLatest
                        }
                        updateMinuteScopeProvider.newScope.launch {
                            delay(newValue - System.currentTimeMillis())
                            adviseDate.postValue(newValue + 60_000)
                        }
                    }
                } catch (e: Exception) { //If coroutine is cancelled
                    updateMinuteScopeProvider.cancel()
                }
            }
        }
    }

    private val _taskTitleChangedEvent = MutableLiveData<ITaskTitleOwner>()
    val taskTitleChangedEvent: LiveData<ITaskTitleOwner> = _taskTitleChangedEvent

    private val _typeChangedEvent = MutableLiveData<TypeChange>()
    val typeChangedEvent: LiveData<TypeChange> = _typeChangedEvent

    val taskDeletedEvent = DataEventTrigger<NoMoreWithTaskDeletedType>()

    private val collectTaskScopeProvider = OneScopeAtOnceProvider()
    private val updateMinuteScopeProvider = OneScopeAtOnceProvider(Dispatchers.Default)

    val creationDateFormat: SimpleDateFormat = dateAndHourFormatProvider.value
    val adviseDateFormat: SimpleDateFormat = dateFormatProvider.value

    val formattedCreationDate = _task.map { task ->
        task ?: return@map ""
        creationDateFormat.format(task.dateNum)
    }
    val formattedAdviseDate = adviseDate.map { numDate ->
        numDate ?: return@map null
        adviseDateFormat.format(numDate)
    }

    fun onSetUp(task: ITaskTitleOwner) {
        collectTaskScopeProvider.newScope.launch {
            var latestCollectedTask: TaskModel? = null
            try {
                getTaskByTitle(task.taskTitle).collectLatest { latestTask ->
                    updateMinuteScopeProvider.cancel()
                    _task.value = latestTask
                    val previousTask = latestCollectedTask

                    if (previousTask == null) {
                        title.value = latestTask.title
                        type.value = latestTask.type
                        description.value = latestTask.description
                        adviseDate.value = latestTask.adviseDate
                    }
                    else if (previousTask.title != latestTask.title) {
                        title.value = latestTask.title
                    }
                    else if (previousTask.type != latestTask.type) {
                        type.value = latestTask.type
                    }
                    else if (previousTask.description != latestTask.description) {
                        description.value = latestTask.description
                    }
                    else if (previousTask.adviseDate != latestTask.adviseDate) {
                        adviseDate.value = latestTask.adviseDate
                    }

                    latestCollectedTask = latestTask
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
                collectTaskScopeProvider.cancel()
                _taskTitleChangedEvent.value = newTitle
            }
        }
    }

    fun saveNewTypeInTaskHierarchy() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTitleOwner(), newType)?.also { newType ->
                _typeChangedEvent.value = task.toSimpleTaskTypeNameOwner() to newType
            }
        }
    }

    fun saveNewType() {
        val newType = type.value ?: return
        val task = _task.value ?: return

        viewModelScope.launch {
            changeType(task.toSimpleTaskTypeNameOwner(), newType)?.also{ newType ->
                _typeChangedEvent.value = task.toSimpleTaskTypeNameOwner() to newType
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

    fun saveNewAdviseDate() {
        val task = _task.value ?: return
        val newAdviseDate = adviseDate.value

        viewModelScope.launch {
            changeAdviseDate(task, newAdviseDate)
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
    fun restoreAdviseDate() {
        adviseDate.value = _task.value?.adviseDate
    }

    fun deleteOnlyTopStackTask() {
        val task = _task.value ?: return
        viewModelScope.launch {
            deleteTask(task).ifTrue {
                collectTaskScopeProvider.cancel()
                taskDeletedEvent.triggerEvent(existsTaskWithType(task.type))
            }
        }
    }

    fun deleteTopStackTaskAndChildren() {
        val task = _task.value ?: return
        viewModelScope.launch {
            deleteTask.alsoChildren(task).ifNotNull {
                collectTaskScopeProvider.cancel()
                taskDeletedEvent.triggerEvent(existsTaskWithType(task.type))
            }
        }
    }

    override fun onCleared() {
        collectTaskScopeProvider.cancel()
        super.onCleared()
    }


    sealed class SavedStatus {
        object Saved: SavedStatus()
        object Savable: SavedStatus()
        object NotSavable: SavedStatus()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("TaskDetailViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

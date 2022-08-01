package com.example.taskscheduler.ui.main.addTask

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.di.util.AppDateFormatProvider
import com.example.taskscheduler.domain.CreateValidTaskUseCase
import com.example.taskscheduler.domain.SaveNewTaskUseCase
import com.example.taskscheduler.util.FirstToSecond
import com.example.taskscheduler.util.SimpleTimeDate
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import com.example.taskscheduler.util.observable.EventTrigger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val saveNewTaskUseCase: SaveNewTaskUseCase,
    dateFormatProvider: AppDateFormatProvider,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val adviseDate = MutableLiveData<Long?>(null)
    val description = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val scopeProvider = OneScopeAtOnceProvider(Dispatchers.Default)
                try {
                    adviseDate.asFlow().collectLatest { newValue ->
                        if (newValue == null) {
                            scopeProvider.cancel()
                            return@collectLatest
                        }
                        scopeProvider.newScope.launch {
                            val timeToUpdateMinute = newValue - System.currentTimeMillis()
                            delay(timeToUpdateMinute)
                            adviseDate.postValue(newValue + 60_000)
                        }
                    }
                } catch (e: Exception) { //If coroutine is cancelled
                    scopeProvider.cancel()
                }
            }
        }
    }

    private val dateFormat = dateFormatProvider.value

    val adviseDateFormatted: LiveData<String?> = adviseDate.map { date ->
        date ?: return@map null
        dateFormat.format(date)
    }

    val notValidAdviseDate = EventTrigger()

    private val isCrated = FirstToSecond(first = false, second = true)

    private var superTaskTitle: String? = null
    val existsSuperTask get() = superTaskTitle != null


    private val _taskHasBeenSaved = MutableLiveData<CreateValidTaskUseCase.Response>()
    val taskHasBeenSaved: LiveData<CreateValidTaskUseCase.Response> = _taskHasBeenSaved

    /** This method must be called in the onCreateView() method of the fragment. */
    fun onCreate(args: AddTaskFragmentArgs) {
        if (isCrated()) return

        isCrated.moveToSecond()
        superTaskTitle = args.supertask

        "AddTaskViewModel created".log()
    }

    fun setAdviseDate(timeDate: SimpleTimeDate) {
        adviseDate.value = timeDate.toCalendar().timeInMillis
    }


    fun save() {
        viewModelScope.launch {
            "Save task coroutine starts".log()

            _taskHasBeenSaved.value = saveNewTaskUseCase(
                title = title.value, type = type.value,
                description = description.value, superTask = superTaskTitle,
                adviseDate = adviseDate.value
            ).log("Response")
        }
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("AddTaskViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

package com.example.taskscheduler.ui.main.addTask

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.di.util.AppDateAndHourFormatProvider
import com.example.taskscheduler.di.util.AppDateFormatProvider
import com.example.taskscheduler.domain.CreateValidTaskUseCase
import com.example.taskscheduler.domain.SaveNewTaskUseCase
import com.example.taskscheduler.util.FirstToSecond
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val saveNewTaskUseCase: SaveNewTaskUseCase,
    dateFormatProvider: AppDateFormatProvider,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    private val adviseDate = MutableLiveData<Long>()
    val description = MutableLiveData<String>()

    private val dateFormat = dateFormatProvider.format

    val adviseDateFormatted: LiveData<String> = adviseDate.map { date ->
        date ?: return@map ""
        dateFormat.format(date)
    }

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

    fun setAdviseDate(year: Int, month: Int, day: Int) {
        val calendar = GregorianCalendar(year, month, day,22,0,0)
        adviseDate.value = calendar.time.time
    }

    /** Called in fragment xml */
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

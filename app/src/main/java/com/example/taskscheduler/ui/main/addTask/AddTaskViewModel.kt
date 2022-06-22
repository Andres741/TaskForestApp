package com.example.taskscheduler.ui.main.addTask

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.CreateValidTaskUseCase
import com.example.taskscheduler.domain.SaveNewTaskUseCase
import com.example.taskscheduler.util.FirstToSecond
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val saveNewTaskUseCase: SaveNewTaskUseCase,
): ViewModel() {

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()

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

    /** Called in fragment xml */
    fun save() {
        viewModelScope.launch {
            "Save task coroutine starts".log()

            _taskHasBeenSaved.value = saveNewTaskUseCase(
                title = title.value, type = type.value,
                description = description.value, superTask = superTaskTitle
            ).log("Response")
        }
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("AddTaskViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

package com.example.taskscheduler.ui.main.addTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.SaveNewTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val saveNewTaskUseCase: SaveNewTaskUseCase,
): ViewModel() {

    private lateinit var superTask: String

    val title = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _taskHasBeenSaved = MutableLiveData<Boolean>()
    val taskHasBeenSaved: LiveData<Boolean> = _taskHasBeenSaved

    /** This method must be called in the onCreateView() method of the fragment. */
    fun onCreate(superTask: String?) {
        if (this::superTask.isInitialized) return

        this.superTask = superTask ?: ""
    }

    /** Called in fragment xml */
    fun save() {
        viewModelScope.launch {
            _taskHasBeenSaved.value = saveNewTaskUseCase (
                title.value, type.value, description.value, superTask
            )!!  //Why don't let me remove this?
        }
    }
}

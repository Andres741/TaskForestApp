package com.example.taskscheduler.ui.main.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.taskscheduler.domain.GetTaskTypePagerUseCase
import com.example.taskscheduler.util.TaskTypeDataFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    getTaskTypePagerUseCase: GetTaskTypePagerUseCase,
): ViewModel() {
    val taskTypeDataFlow: TaskTypeDataFlow = getTaskTypePagerUseCase().cachedIn(viewModelScope)

//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i("TasksViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
//    }
}

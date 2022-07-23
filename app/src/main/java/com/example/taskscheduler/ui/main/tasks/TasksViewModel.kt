package com.example.taskscheduler.ui.main.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.taskscheduler.domain.GetTaskTypePagerUseCase
import com.example.taskscheduler.util.TaskTypeDataFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    getTaskTypePager: GetTaskTypePagerUseCase,
): ViewModel() {
    val taskTypeDataFlow: TaskTypeDataFlow = getTaskTypePager().cachedIn(viewModelScope)

    val isShowingOnlyTopSuperTask = MutableLiveData(false)

//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i("TasksViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
//    }
}

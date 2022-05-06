package com.example.taskscheduler.ui.main.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskSchedulerApp
import com.example.taskscheduler.domain.GetTaskPagerUseCase
import com.example.taskscheduler.domain.models.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTaskPagerUseCase: GetTaskPagerUseCase
): ViewModel() {

    val msjTxt = getString(R.string.hello_first_fragment)
    val msjTxtI = getString(R.string.hello_first_fragment)
    val msj = MutableLiveData(msjTxt)

    //TODO: Decide which of the following two I will use.
    val pagingDataFlow: Flow<PagingData<TaskModel>> = getTaskPagerUseCase().flow
    val pagingLiveData: LiveData<PagingData<TaskModel>> = getTaskPagerUseCase().liveData


    companion object {
        fun getString(resource: Int) = TaskSchedulerApp.INSTANCE.getString(resource)
    }
}

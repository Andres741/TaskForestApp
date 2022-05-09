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
import com.example.taskscheduler.ui.adapters.fragmentAdapters.TaskAdapterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(

): ViewModel() {


    companion object {
        fun getString(resource: Int) = TaskSchedulerApp.INSTANCE.getString(resource)
    }
}

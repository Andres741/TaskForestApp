package com.example.taskscheduler.ui.main.taskDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.data.models.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(): ViewModel() {

    val task: MutableLiveData<TaskModel> = TODO("Navigation not implemented yet.")

}

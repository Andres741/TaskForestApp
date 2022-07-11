package com.example.taskscheduler.ui.main

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val taskRepository: ITaskRepository,
): ViewModel() {

}

package com.example.taskscheduler.ui.main.tasks

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskSchedulerApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    val msjTxt = TaskSchedulerApp.INSTANCE.getString(R.string.hello_first_fragment)
    val msjTxtI = TaskSchedulerApp.INSTANCE.getString(R.string.hello_first_fragment)
    val msj = MutableLiveData(msjTxt)
}

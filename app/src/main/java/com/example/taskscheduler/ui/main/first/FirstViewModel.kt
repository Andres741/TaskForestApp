package com.example.taskscheduler.ui.main.first

import android.R.string
import android.content.Context
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskSchedulerApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class FirstViewModel @Inject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    val msjTxt = TaskSchedulerApp.INSTANCE.getString(R.string.hello_first_fragment)
    val msjTxtI = TaskSchedulerApp.INSTANCE.getString(R.string.hello_first_fragment)
    val msj = MutableLiveData(msjTxt)
}

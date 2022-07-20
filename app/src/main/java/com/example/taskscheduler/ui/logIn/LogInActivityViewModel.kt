package com.example.taskscheduler.ui.logIn

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.util.observable.EventTrigger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInActivityViewModel @Inject constructor(): ViewModel() {
    val goToMainActivity = EventTrigger()
}

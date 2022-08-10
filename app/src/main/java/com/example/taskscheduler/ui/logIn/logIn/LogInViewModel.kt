package com.example.taskscheduler.ui.logIn.logIn

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.FinishFirebaseSessionUseCaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    finishFirebaseSessionUseCaseAuth: FinishFirebaseSessionUseCaseAuth
): ViewModel() {
    private val startFirebaseSession = finishFirebaseSessionUseCaseAuth.value

    fun startFirebaseSession() {
        startFirebaseSession?.start()
    }
}

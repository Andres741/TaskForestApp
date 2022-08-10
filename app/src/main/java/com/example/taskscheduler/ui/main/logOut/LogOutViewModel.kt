package com.example.taskscheduler.ui.main.logOut

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.FinishFirebaseSessionUseCaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogOutViewModel @Inject constructor(
    finishFirebaseSessionUseCaseAuth: FinishFirebaseSessionUseCaseAuth
): ViewModel() {
    private val finishFirebaseSession = finishFirebaseSessionUseCaseAuth.value

    fun finishFirebaseSession() {
        finishFirebaseSession?.finish()
    }
}

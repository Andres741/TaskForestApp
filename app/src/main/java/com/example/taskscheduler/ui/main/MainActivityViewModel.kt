package com.example.taskscheduler.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.SynchronizeFromFirestoreUseCaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    synchronizeFromFirestoreUseCaseAuth: SynchronizeFromFirestoreUseCaseAuth,
): ViewModel() {
    private val synchronizeFromFirestore = synchronizeFromFirestoreUseCaseAuth.synchronizeFromFirestoreUseCase

    fun synchronizeTasks(): Boolean {
        val synchronizeFromFirestore = synchronizeFromFirestore ?: return false
        viewModelScope.launch {
            try {
                val time = measureTimeMillis {
                    synchronizeFromFirestore()
                }
                "Firestore synchronization succeed in $time millis".log()
            }
            catch (IO: IOException) {
                "Not possible to connect with firestore".log()
                IO.log()
            }
            catch (timeOut: TimeoutCancellationException) {
                "It took too long to connect with firestore".log()
                timeOut.log()
            }
        }
        return true
    }


    private fun<T> T.log(msj: Any? = null) = apply {
        Log.i("MainActivityViewModel", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
//    private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
//        "$msj:".uppercase().log()
//        this.iterator().hasNext().ifTrue {
//            "  Collection is empty".log()
//            return@apply
//        }
//        forEachIndexed { index, elem ->
//            elem.log(index)
//        }
//    }
}

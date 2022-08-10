package com.example.taskscheduler.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.SynchronizeFromFirestoreUseCaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    synchronizeFromFirestoreUseCaseAuth: SynchronizeFromFirestoreUseCaseAuth,
): ViewModel() {

    private val synchronizeFromFirestore = synchronizeFromFirestoreUseCaseAuth.synchronizeFromFirestoreUseCase

    val intentChannel = Channel<Intent>()
    private val _syncStateFlow = MutableStateFlow<SyncState>(SyncState.NotAuth)
    val syncStateFlow: StateFlow<SyncState> = _syncStateFlow

    init {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when(intent) {
                    is Intent.Synchronize -> synchronizeTasks()
                }
            }
        }
    }

    private suspend fun synchronizeTasks() {
        val synchronizeFromFirestore = synchronizeFromFirestore ?: return
        "Sync starts".log()
        _syncStateFlow.value = SyncState.InProcess

        _syncStateFlow.value = try {
            val time = measureTimeMillis {
                synchronizeFromFirestore()
            }
            "Firestore synchronization succeed in $time millis".log()
            SyncState.Done
        }
        catch (IO: IOException) {
            "Not possible to connect with firestore".log()
            IO.log()
            SyncState.Error
        }
        catch (timeOut: TimeoutCancellationException) {
            "It took too long to connect with firestore".log()
            timeOut.log()
            SyncState.Error
        }
        catch (e: Exception) {
            "A exception has occurred".log()
            e.log()
            SyncState.Error

        }
    }


    override fun onCleared() {
        super.onCleared()
        intentChannel.close()
    }

    sealed class SyncState {
        object Done: SyncState()
        object InProcess: SyncState()
        object Error: SyncState()
        object NotAuth: SyncState()
    }
    sealed class Intent {
        object Synchronize: Intent()
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

package com.example.taskscheduler.util.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OneScopeAtOnceProvider (
    private val coroutineContext: CoroutineContext = Dispatchers.Main
) {

    var currentScope: CoroutineScope? = null
        private set

    val newScope: CoroutineScope
        get() {
            currentScope?.cancel()
            return CoroutineScope(coroutineContext).also { currentScope = it }//(::currentScope.setter) does not work due to private set
        }

    val currentScopeOrNew get() = currentScope ?: newScope

    val newScopeNotCancelCurrentOrNull get() = if (currentScope == null) newScope else null

    fun cancel(): Boolean = currentScope?.run {
        currentScope = null
        cancel()
        true
    } ?: false
}

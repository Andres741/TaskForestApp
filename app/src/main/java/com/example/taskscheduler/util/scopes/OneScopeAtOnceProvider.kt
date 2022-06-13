package com.example.taskscheduler.util.scopes

import kotlinx.coroutines.*

class OneScopeAtOnceProvider (
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    var currentScope: CoroutineScope? = null
        private set

    val newScope: CoroutineScope
        get() {
            currentScope?.cancel()
            return CoroutineScope(Job() + dispatcher).also { currentScope = it }//(::scope.setter) does not work due to private set
        }

    fun cancel(): Boolean = currentScope?.run {
        currentScope = null
        cancel()
        true
    } ?: false
}

package com.example.taskscheduler.util.observable

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

open class DataEventTrigger<T> {
    protected val eventManager = MutableLiveData<T?>(null)

    /**
     * A call to this function trigger a call to onEvent once.
     */
    open fun triggerEvent(data: T) {
        eventManager.value = data
    }

    //inline operator fun invoke() = triggerEvent()

    fun setEvent(owner: LifecycleOwner, onEvent: (T)-> Unit) {
        eventManager.value = null
        eventManager.observe(owner) event@ { data ->
            data?: return@event
            onEvent(data)
            eventManager.value = null
        }
    }
}

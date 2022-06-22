package com.example.taskscheduler.util.observable

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class EventTrigger {
    private val eventManager = MutableLiveData<Unit?>(null)

    fun triggerEvent() {
        eventManager.value = Unit
    }

    //inline operator fun invoke() = triggerEvent()

    fun setEvent(owner: LifecycleOwner, onEvent: ()-> Unit) {
        eventManager.value = null
        eventManager.observe(owner) event@{
            it?: return@event
            onEvent()
        }
    }
}

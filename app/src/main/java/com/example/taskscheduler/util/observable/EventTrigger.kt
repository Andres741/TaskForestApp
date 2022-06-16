package com.example.taskscheduler.util.observable

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class EventTrigger {
    private val liveData = MutableLiveData<Unit?>(null)

    fun triggerEvent() {
        liveData.value = Unit
    }

    //inline operator fun invoke() = triggerEvent()

    fun setEvent(owner: LifecycleOwner, onEvent: ()-> Unit) {
        liveData.value = null
        liveData.observe(owner) event@{
            it?: return@event
            onEvent()
        }
    }
}

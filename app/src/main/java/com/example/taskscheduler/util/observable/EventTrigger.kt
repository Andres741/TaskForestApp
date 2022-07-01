package com.example.taskscheduler.util.observable


class EventTrigger: DataEventTrigger<Unit>() {
    fun triggerEvent() {
        eventManager.value = Unit
    }

    override fun triggerEvent(data: Unit) {
        triggerEvent()
    }
}

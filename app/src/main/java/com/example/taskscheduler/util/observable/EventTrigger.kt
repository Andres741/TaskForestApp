package com.example.taskscheduler.util.observable


class EventTrigger: DataEventTrigger<Unit>() {
    fun triggerEvent() {
        eventManager.value = Unit
    }

    inline operator fun invoke() = triggerEvent()

    override fun triggerEvent(data: Unit) {
        triggerEvent()
    }
}

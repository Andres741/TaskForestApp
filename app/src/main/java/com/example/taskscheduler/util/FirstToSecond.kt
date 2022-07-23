package com.example.taskscheduler.util

/** Value is first until moveToSecond(), and then value is always second. */
class FirstToSecond<T>(
    first: T,
    private val second: T,
) {
    var value = first
        private set

    fun moveToSecond() {
        value = second
    }

    inline operator fun invoke() = value
}

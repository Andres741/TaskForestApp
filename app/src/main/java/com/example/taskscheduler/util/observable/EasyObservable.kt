package com.example.taskscheduler.util.observable

class EasyObservable<T> (
    value: T? = null,
    var observer: (T?) -> Unit = {  },
) {
    var value: T? = value
        set(value) {
            observer(value)
            field = value
        }
}


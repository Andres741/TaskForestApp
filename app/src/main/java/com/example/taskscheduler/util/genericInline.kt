package com.example.taskscheduler.util

inline fun <T> T?.returnThisIfError(target: ()->T?): T? {
    return try {
        target()
    } catch (e: Error) {
        this
    }
}

inline fun <T> T.returnThisIfErrorOrNull(target: ()->T?): T {
    return try {
        target() ?: this
    } catch (e: Error) {
        this
    }
}

inline fun <T> returnNullIfError(target: ()->T?): T? {
    return try {
        target()
    } catch (e: Error) {
        null
    }
}

inline fun <T> returnDefaultIfErrorOrNull(default: ()->T, target: ()->T?): T {
    return returnNullIfError(target) ?: default()
}

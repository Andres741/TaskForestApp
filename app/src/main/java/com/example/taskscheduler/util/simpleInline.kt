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

inline fun<B: Boolean?> B.ifTrue(block: () -> Unit): B = apply { if (this == true) block() }

inline fun<B: Boolean?> B.ifFalse(block: () -> Unit): B = apply { if (this == false) block() }

inline fun <T> T?.ifNull(block: () -> Unit) = apply { if (this == null) block() }

inline fun <T> T?.ifNotNull(block: () -> Unit) = apply { if (this != null) block() }

inline fun Collection<*>?.isNotNullOrEmpty() = !isNullOrEmpty()

inline fun <V> Map<*, V>.notContainsValue(value: V) = !containsValue(value)

inline fun <K> Map<K, *>.notContainsKey(key: K) = !containsKey(key)

inline fun <T> Collection<T>.notContains(value: T) = !contains(value)


package com.example.taskscheduler.util

//inline fun <T> T?.returnThisIfException(target: ()->T?): T? {
//    return try {
//        target()
//    } catch (e: Error) {
//        this
//    }
//}
//
//inline fun <T> T.returnThisIfExceptionOrNull(target: ()->T?): T {
//    return try {
//        target() ?: this
//    } catch (e: Error) {
//        this
//    }
//}
//
//inline fun <T> returnNullIfException(target: ()->T): T? {
//    return try {
//        target()
//    } catch (e: Error) {
//        null
//    }
//}
//
//inline fun <T> returnDefaultIfExceptionOrNull(default: ()->T, target: ()->T?): T {
//    return returnNullIfException(target) ?: default()
//}

inline fun <B: Boolean?> B.ifTrue(block: () -> Unit): B = apply { if (this == true) block() }

inline fun <B: Boolean?> B.ifFalse(block: () -> Unit): B = apply { if (this == false) block() }

inline fun <T> T.ifNull(block: () -> Unit) = also { if (this == null) block() }

inline fun <T: Any> T?.ifNotNull(block: (T) -> Unit) = also { if (it != null) block(it) }

inline fun Collection<*>?.isNotNullOrEmpty() = !isNullOrEmpty()

inline fun <V> Map<*, V>.notContainsValue(value: V) = !containsValue(value)

inline fun <K> Map<K, *>.notContainsKey(key: K) = !containsKey(key)

inline fun <T> Collection<T>.notContains(value: T) = !contains(value)

inline fun <T> Iterable<(T)-> Boolean>.or(value: T): Boolean? = iterator().run {
    if (! hasNext()) return null
    forEach {
        if (it(value)) return true
    }
    return false
}

inline fun <T> Iterable<(T)-> Boolean>.and(value: T): Boolean? = iterator().run {
    if (! hasNext()) return null
    forEach {
        if (! it(value)) return false
    }
    return true
}

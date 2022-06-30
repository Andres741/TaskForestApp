package com.example.taskscheduler.util

import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.util.dataStructures.MyLinkedList

fun<T> Iterable<T>.toUnitHashMap(): HashMap<T, Unit> {
    if (this is Collection<T>) return toUnitHashMap()

    val list: MyLinkedList<T> = MyLinkedList(this)
    val map = HashMap<T, Unit>(list.size)

    list.normalIterator().forEach { item ->
        map[item] = Unit
    }
    return map
}

fun<T> Collection<T>.toUnitHashMap() = HashMap<T, Unit>(size).also { map ->
    forEach { item: T ->
        map[item] = Unit
    }
}

fun<T> Iterable<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Iterable<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> Collection<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Collection<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> MutableLiveData<T>.observeAgain() { value = value }

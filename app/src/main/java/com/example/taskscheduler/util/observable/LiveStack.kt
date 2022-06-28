package com.example.taskscheduler.util.observable

import androidx.lifecycle.LiveData
import com.example.taskscheduler.util.dataStructures.MyLinkedList

/**
 * Union between LiveData and Stack where like a stack FILO logic is implemented and the top of the stack
 * is observable.
 */
open class LiveStack<T> private constructor(
    private val stack: MyLinkedList<T>
): LiveData<T>(), List<T> by stack {

    constructor(): this( MyLinkedList() )

    override fun getValue(): T? {
        return stack.getFirst()
    }

    fun add(value: T) {
        stack.addFirst(value)
        super.setValue(value)
    }

    fun remove() {
        stack.removeFirst()
        super.setValue(stack.getFirst())
    }

    fun pop() = stack.pop().also { super.setValue(stack.getFirst()) }

    fun clear() {
        stack.clear()
        super.setValue(null)
    }

    fun changeTop(newValue: T) {
        stack.setFirst(newValue)
        super.setValue(newValue)
    }

    fun notifyObserveAgain() {
        value = value
    }

    override fun iterator() = stack.normalIterator()
}

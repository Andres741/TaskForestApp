package com.example.taskscheduler.util.observable

import androidx.lifecycle.LiveData
import com.example.taskscheduler.util.dataStructures.LinkedList

/**
 * Union between LiveData and Stack where like a stack FILO logic is implemented and the top of the stack
 * is observable.
 */
class LiveStack<T> : LiveData<T>(), Collection<T> {
    private val stack = LinkedList<T>()
    val elements: List<T> = stack

    override fun getValue(): T? {
        return stack.getFirst()
    }

    fun add(value: T) {
        stack.addFirst(value)
        super.setValue(value)
    }

    fun remove() = stack.remove().also { super.setValue(stack.getFirst()) }

    fun pop() = stack.pop().also { super.setValue(stack.getFirst()) }

    override fun isEmpty() = stack.isEmpty()

    fun isNotEmpty() = stack.isNotEmpty()

    override val size = stack.size

    override fun contains(element: T) = stack.contains(element)

    override fun containsAll(elements: Collection<T>) = stack.containsAll(elements)

    override fun iterator(): Iterator<T> = stack.iterator()

}

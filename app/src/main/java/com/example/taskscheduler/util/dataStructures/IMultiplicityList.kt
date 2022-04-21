package com.example.taskscheduler.util.dataStructures

interface IMultiplicityList<T> {
    /**
     * The keys are the items of the list, and the value the multiplicity.
     */
    val _elements: LinkedHashMap<T, Int>
    val elements get() = _elements.entries
    val keyList: List<T> get() = _elements.keys.toList()
    val size: Int get() = _elements.size
    val isEmpty: Boolean get() = _elements.isEmpty()

    /**
     * Returns a item of type T of the list.
     */
    operator fun get(index: Int): T

    /**
     * Returns the multiplicity of a item of the list, or null if the list does not contains the item.
     */
    fun multiplicityOf(item: T): Int?

    operator fun contains(item: T): Boolean

    fun add(item: T)

    fun remove(index: Int)

    fun remove(item: T)
}
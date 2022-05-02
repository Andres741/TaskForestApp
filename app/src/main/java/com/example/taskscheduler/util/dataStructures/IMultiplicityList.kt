package com.example.taskscheduler.util.dataStructures

interface IMultiplicityList<T>: Iterable<Pair<T, Int>> {
    /**
     * The keys are the items of the list, and the value the multiplicity.
     */
    val elements: Set<Map.Entry<T, Int>>
    val keyList: List<T>
    val size: Int get() = elements.size
    val isEmpty: Boolean get() = elements.isEmpty()

    /**
     * Returns a item of type T of the list.
     */
    operator fun get(index: Int): Pair<T, Int>

    /**
     * Returns the multiplicity of a item of the list, or null if the list does not contains the item.
     */
    fun multiplicityOf(item: T): Int?

    operator fun contains(item: T): Boolean

    fun insert(item: T)

    fun insertPair(item: Pair<T, Int>)

    fun insertAll(items: Iterable<T>)

    fun insertAllPair(items: Iterable<Pair<T, Int>>)

    fun remove(index: Int)

    fun remove(item: T)
}
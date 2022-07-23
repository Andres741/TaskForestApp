package com.example.taskscheduler.util.dataStructures

class MultiplicityList<T>() : IMultiplicityList<T> {
    /**
     * The keys are the items of the list, and the value the multiplicity.
     */
    val _elements: MutableMap<T, Int> = LinkedHashMap<T, Int>()

    override val elements: Set<Map.Entry<T, Int>> get() = _elements.entries

    override val keyList: List<T> get() = _elements.keys.toList()

    constructor(elems: Iterable<Pair<T, Int>>): this() {
        elems.forEach {
            _elements[it.first] = it.second
        }
    }

    /**
     * Returns a item of type T of the list.
     */
    override operator fun get(index: Int) = run {
        val elem = keyList[index]
        elem to multiplicityOf(elem)
    }

    /**
     * Returns the multiplicity of a item of the list, or null if the list does not contains the item.
     */
    override infix fun multiplicityOf(item: T): Int = _elements[item] ?: 0

    override operator fun contains(item: T) = _elements.containsKey(item)

    override fun insert(item: T) {
        _elements[item] = 1 + multiplicityOf(item)
    }

    override fun insertPair(item: Pair<T, Int>) {
        if (item.second < 1) throw IllegalArgumentException("Multiplicity have to be greater than zero.")
        _elements[item.first] = item.second + multiplicityOf(item.first)
    }

    override fun remove(index: Int) {
        remove(keyList[index])
    }

    override fun remove(item: T) {
        val newMult = -1 + multiplicityOf(item)
        if (newMult > 0) _elements[item] = newMult
        else _elements.remove(item)
    }

    override fun iterator() = object: Iterator<Pair<T, Int>> {
        val iter = _elements.iterator()

        override fun hasNext() = iter.hasNext()

        override fun next(): Pair<T, Int> = iter.next().toPair()
    }

    override fun insertAll(items: Iterable<T>) = items.forEach(::insert)

    override fun insertAllPair(items: Iterable<Pair<T, Int>>) = items.forEach(::insertPair)

}

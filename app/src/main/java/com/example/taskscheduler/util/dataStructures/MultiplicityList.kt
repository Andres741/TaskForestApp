package com.example.taskscheduler.util.dataStructures

class MultiplicityList<T> : IMultiplicityList<T> {
    /**
     * The keys are the items of the list, and the value the multiplicity.
     */
    override val _elements = LinkedHashMap<T, Int>()

    /**
     * Returns a item of type T of the list.
     */
    override operator fun get(index: Int) = keyList[index]

    /**
     * Returns the multiplicity of a item of the list, or null if the list does not contains the item.
     */
    override fun multiplicityOf(item: T) = _elements[item]

    override operator fun contains(item: T) = _elements.containsKey(item)

    override fun add(item: T) {
        val multi = multiplicityOf(item)

        if (multi != null) {

            _elements[item] = multi + 1

        } else {
            _elements[item] = 1
        }
    }

    override fun remove(index: Int) {
        remove(keyList[index])
    }

    override fun remove(item: T) {
        _elements.remove(item)
    }

}

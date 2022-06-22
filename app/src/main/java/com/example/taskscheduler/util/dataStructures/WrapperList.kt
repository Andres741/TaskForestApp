package com.example.taskscheduler.util.dataStructures

import com.example.taskscheduler.util.unitHashMap

/**
 * Ideal for making list from a property of the objects of a list, otherwise it could be slow.
 */
class WrapperList<T, R> (
    val originalList: List<T>,
    val converter: (T) -> R,
): List<R> {

    override val size get() = originalList.size

    override fun contains(element: R): Boolean {
        originalList.forEach {
            if (converter(it) == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<R>): Boolean {
        val map = elements.unitHashMap()
        originalList.forEach {
            if (!map.containsKey(converter(it))) return false
        }
        return true
    }

    override fun get(index: Int) = converter(originalList[index])

    override fun indexOf(element: R): Int {
        var res = -1
        originalList.forEachIndexed loop@ { index, it ->
            if (converter(it) == element) {
                res = index
                return@loop
            }
        }
        return res
    }

    override fun lastIndexOf(element: R): Int {
        var res = -1
        originalList.forEachIndexed loop@ { index, it ->
            if (converter(it) == element) {
                res = index
            }
        }
        return res
    }

    override fun isEmpty() = originalList.isEmpty()

    override fun iterator() = object: Iterator<R> {
        val iter = originalList.iterator()
        override fun hasNext() = iter.hasNext()
        override fun next() = converter(iter.next())
    }

    override fun listIterator() = WrapperListIterator(originalList.listIterator(), converter)
    override fun listIterator(index: Int) = WrapperListIterator(originalList.listIterator(index), converter)

    override fun subList(fromIndex: Int, toIndex: Int) = WrapperList(
        originalList.subList(fromIndex, toIndex), converter
    )

    class WrapperListIterator<T, R>(
        private val originalListIterator: ListIterator<T>,
        private val converter: (T) -> R,
    ): ListIterator<R> {
        override fun hasNext() = originalListIterator.hasNext()
        override fun hasPrevious() = originalListIterator.hasPrevious()
        override fun next() = converter(originalListIterator.next())
        override fun nextIndex() = originalListIterator.nextIndex()
        override fun previous() = converter(originalListIterator.previous())
        override fun previousIndex() = originalListIterator.previousIndex()
    }
}

inline fun <reified T, reified R> List<Pair<T,R>>.wrapperUnzip(): Pair< WrapperList<Pair<T,R>, T>, WrapperList<Pair<T,R>, R> > {
    val first = WrapperList(this, Pair<T,R>::first)
    val second = WrapperList(this, Pair<T,R>::second)
    return first to second
}

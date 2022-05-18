package com.example.taskscheduler.util.dataStructures

import com.example.taskscheduler.util.errors.outOfBoundsOf

/**
 * A implementation of the data structure linked list. The new elements are added by default at the end,
 * and removed at the beginning like in the queues.
 */
class LinkedList<T>(): List<T> {  //TODO: Make this class a MutableList
    /**
     * First node of the list, it will be null only when the list is empty
     * The new nodes are going to be added here.
     */
    private var first: Node? = null
    /**
     * First node of the last, it will be null only when the list is empty
     * By default the deleted node will be this.
     */
    private var last: Node? = null

    override var size = 0
        private set

    override fun isEmpty() = size == 0
    fun isNotEmpty() = !isEmpty()
    
    private constructor(first: LinkedList<T>.Node, last: LinkedList<T>.Node) : this() {
        this.first = first
        this.last = last
    }


    override operator fun get(index: Int): T {
        if (index == size-1) return last!!.elem
        return getNode(index).elem
    }

    fun getFirst() = first?.elem
    fun getLast() = last?.elem

    override fun equals(other: Any?): Boolean {
        if (other !is LinkedList<*>) return false

        if (size != other.size) return false

        if(isEmpty()) return true  //Because both have the same size.

        // First is null only when the list is empty.
        var nodeThis = first!!
        var nodeOther = other.first!!

        while (nodeThis.elem == nodeOther.elem) {
            //Because both have the same size they reach the end of the list (null) at the same time.
            nodeThis = nodeThis.next ?: return true
            nodeOther = nodeOther.next!!
        }
        //If two nodes of the list are different the whole list is different
        return false
    }
    
    fun getOrNull(index: Int): T? {
        return getNodeOrNull(index)?.elem
    }

    /**
     * Adds an element at the end of the list.
     */
    fun add(value: T) {
        if (isEmpty()) {
            Node(value).apply {
                first = this
                last = this
            }
            size = 1
            return
        }
        last!!.next = Node(value)
        last = last!!.next!!
        size++
    }

    fun addFirst(value: T) {
        val newFirst = Node(value)
        newFirst.next = first
        first = newFirst

        if (isEmpty()) {
            last = newFirst
            size = 1
            return
        }
        size++
    }

    fun addAll(values: Iterable<T>) {
        val it = values.iterator()
        if (!it.hasNext()) {
            return
        }

        val firstNewNode = Node(it.next())
        var lastNewNode = firstNewNode

        while (it.hasNext()) {
            lastNewNode.next = Node(it.next())
            lastNewNode = lastNewNode.next!!
        }

        if (isEmpty()) {
            first = firstNewNode
            last = lastNewNode
            return
        }

        last!!.next = firstNewNode
        last = lastNewNode
    }

    fun set(index: Int, value: T) {
        getNode(index).elem = value
    }

    fun removeAt(index: Int) {
        if (index == 0) {
            remove()
            return
        }
        val prevNode = getNode(index - 1)

        if (index == size-1) {
            prevNode.next = null
            last = prevNode
            size--
            return
        }
        val postNode = prevNode.next!!.next
        prevNode.next = postNode
        size--
    }

    /**
     * Deletes the first element.
     */
    fun remove() {
//        if (last === first) { // The list is empty, or only are one node.
//            last = null
//            first = null
//            size = 0
//            return
//        }
        if (isEmpty()) return

        if (size == 1){
            last = null
            first = null
            size = 0
            return
        }
        first = first!!.next
        size--
    }

    /**
     * Deletes the first element and returns it.
     */
    fun pop(): T? {
        val lastFirstElem = first?.elem
        remove()
        return lastFirstElem
    }

    override operator fun iterator() = object: Iterator<T> {
        var currentNode = first

        override fun hasNext() = currentNode != null

        override fun next(): T {
            val res = currentNode!!.elem
            currentNode = currentNode!!.next
            return res
        }
    }

    inline fun<R> map(transform: (T)-> R) = LinkedList<R>().also { res ->
        forEach { elem ->
            res.add(transform(elem))
        }
    }

    override fun contains(element: T): Boolean {
        forEach { elem ->
            if (elem == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val map = toHashMap()

        for (element in elements) {
            if (map.containsValue(element)) return true
        }
        return false
    }

    override fun indexOf(element: T): Int {
        var it = first ?: return -1
        var cont = 0
        while (true) {
            if (it.elem == element) return cont
            it = it.next ?: return -1
            cont++
        }
    }

    override fun lastIndexOf(element: T): Int {
        var it = first ?: return -1
        var cont = 0
        var last = -1
        while (true) {
            if (it.elem == element) last = cont
            it = it.next ?: break
            cont++
        }
        return last
    }

    override fun listIterator(): ListIterator<T> = toList().listIterator()

    override fun listIterator(index: Int): ListIterator<T> = toList().listIterator()

    override fun subList(fromIndex: Int, toIndex: Int): LinkedList<T> {
        val cant = toIndex - fromIndex
        if(cant <= 0) return LinkedList()

        val first = getNode(fromIndex)
        var last = first
        for (i in 0 until cant) {
            last = last.next ?: throw IndexOutOfBoundsException()
        }
        return LinkedList(first, last)
    }

    /**
     * Returns a HashMap where the keys are numbers, like an array.
     */
    fun toHashMap() = HashMap<Int, T>().also { map ->
        forEachIndexed { index, elem ->
            map[index] = elem
        }
    }

    override fun toString() = StringBuilder().run {
        val iter = this@LinkedList.iterator()

        append("[")
        if (iter.hasNext()) {
            append(iter.next())
        }
        while (iter.hasNext()) {
            append(", ")
            append(iter.next())
        }
        append("]")

        toString()
    }


    private inner class Node(
        var elem: T,
        var next: Node? = null
    ) {
        fun getNodeOrNull(index: Int): Node? {
            var it = this

            for (i in 0 until index){
                it = it.next ?: return null
            }
            return it
        }
        fun getNode(index: Int): Node = getNodeOrNull(index) ?: throw size outOfBoundsOf index
    }

    private fun getNode(index: Int): Node {
        if(index == size-1) {
            return last ?: throw size outOfBoundsOf index
        }
        var currentNode = first ?: throw size outOfBoundsOf index
        for (i in 0 until index) {
            currentNode = currentNode.next ?: throw size outOfBoundsOf index
        }
        return currentNode
    }
    private fun getNodeOrNull(index: Int): Node? {
        if(index == size-1) {
            return last
        }
        var currentNode = first ?: return null
        for (i in 0 until index) {
            currentNode = currentNode.next ?: return null
        }
        return currentNode
    }
}

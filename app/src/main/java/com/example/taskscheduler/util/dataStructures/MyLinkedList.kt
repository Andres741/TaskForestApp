package com.example.taskscheduler.util.dataStructures

import android.util.Log
import com.example.taskscheduler.util.containsInConstantTime
import com.example.taskscheduler.util.dataStructures.Node.Companion.createChainAndGetSize
import com.example.taskscheduler.util.errors.outOfBoundsOf
import com.example.taskscheduler.util.notContainsInConstantTime

/**
 * A implementation of the data structure linked list. The new elements are added by default at the end,
 * and removed at the beginning like in the queues.
 */
class MyLinkedList<T>(): MutableList<T> {
    /**
     * First node of the list, it will be null only when the list is empty
     * The new nodes are going to be added here.
     */
    private var first: Node<T>? = null
    /**
     * First node of the last, it will be null only when the list is empty
     * By default the deleted node will be this.
     */
    private var last: Node<T>? = null

    override var size = 0
        private set

    override fun isEmpty() = size == 0

    fun isNotEmpty() = size > 0

    constructor(first: Node<T>, last: Node<T>, size: Int) : this() {
        this.first = first
        this.last = last
        this.size = size
    }

    private constructor(data: Triple<Node<T>, Node<T>, Int>?) : this() {
        data ?: return
        this.first = data.first
        this.last = data.second
        this.size = data.third
    }

    constructor(vararg elements: T): this() {
        val (first, last) = Node.createChain(elements.iterator()) ?: return

        this.first = first
        this.last = last
        this.size = elements.size
    }

    constructor(elements: Iterator<T>): this(Node.createChainIf(elements){ true })

    private constructor(elements: Iterator<T>, size: Int): this() {
        val (first, last) = Node.createChain(elements) ?: return

        this.first = first
        this.last = last
        this.size = size
    }

    constructor(elements: Iterable<T>): this(elements.iterator())

    constructor(elements: Collection<T>): this(elements.iterator(), elements.size)

    constructor(linkedList: MyLinkedList<T>): this(linkedList.normalIterator(), linkedList.size)

    constructor(generator: () -> T?): this(Node.createChain(generator))

    override operator fun get(index: Int): T {
        if (index == size-1) return last!!.elem
        return getNode(index).elem
    }

    fun getFirst() = first?.elem

    fun getLast() = last?.elem

    fun setFirst(element: T): T {
        val first = first
        val res: T = first!!.elem

        first.elem = element
        return res
    }

    fun setLast(element: T): T {
        val last = last
        val res: T = last!!.elem

        last.elem = element
        return res
    }


    override fun equals(other: Any?): Boolean {
        if (other !is MyLinkedList<*>) return false

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

    fun clearListAndAdd(element: T) {
        Node(element).apply {
            first = this
            last = this
        }
        size = 1
    }

    /**Don't call in empty LinkedList.*/
    private inline fun addAtTheEndNotEmpty(element: T) {
        last!!.next = Node(element)
        last = last!!.next!!
        size++
    }

    override fun add(element: T): Boolean {
        if (isEmpty()) {
            clearListAndAdd(element)
        } else {
            addAtTheEndNotEmpty(element)
        }
        return true
    }

    override fun add(index: Int, element: T) {
        if (isEmpty()) {
            clearListAndAdd(element)
            return
        }
        val prevNode = first!![index]

        if (prevNode?.next == null) {
            addAtTheEndNotEmpty(element)
            return
        }
        val nextNode = prevNode.next!!
        val newNode = Node(element, nextNode)
        prevNode.next = newNode
    }

    fun addFirst(value: T) {
        val newFirst = Node(value, first)
        first = newFirst

        if (isEmpty()) {
            last = newFirst
            size = 1
            return
        }
        size++
    }

    fun addAll(values: Iterable<T>) {
        val (firstNode, lastNode, chainSize) = createChainAndGetSize(values) ?: return
        if (isEmpty()) {
            first = firstNode
            last = lastNode
            size = chainSize
            return
        }
        last!! += firstNode
        last = lastNode
        size += chainSize
    }

    override fun set(index: Int, element: T): T {
        val node = getNode(index)
        val res = node.elem
        node.elem = element
        return res
    }

    override fun removeAt(index: Int): T {
        if (index == 0) {
            val res = getFirst()
            removeFirst()
            return res!!
        }
        val prevNode = getNode(index - 1)
        val res = prevNode.next!!.elem

        if (index == size-1) {

            prevNode.next = null
            last = prevNode
            size--
            return res
        }
        val postNode = prevNode.next!!.next
        prevNode.next = postNode
        size--
        return res
    }

    /**
     * Deletes the first element.
     */
    fun removeFirst() {
        if (isEmpty()) return

        if (size == 1){
            clear()
            return
        }
        first = first!!.next
        size--
    }

    override fun clear() {
        val previousFirst = first ?: return
        first = null
        last = null
        size = 0
        previousFirst.destroyChain()
    }


    /**
     * Deletes the first element and returns it.
     */
    fun pop(): T? = first?.elem.apply { removeFirst() }

    override operator fun iterator() = object: MutableIterator<T> {

        val iterator = Node.iterator(first)

        override fun hasNext() = iterator.hasNext()

        override fun next(): T = iterator.next().elem

        override fun remove() = iterator.remove()
    }

    fun normalIterator() = object: Iterator<T> {

        var currentNode = first

        override fun hasNext() = currentNode != null

        override fun next(): T {
            val res = currentNode!!.elem
            currentNode = currentNode!!.next
            return res
        }
    }

    inline fun<R> mapMyLinkedList(transform: (T)-> R): MyLinkedList<R> {
        val (firstNode, lastNode) =
            Node.createTransformedChain(normalIterator(), transform) ?: return MyLinkedList()
        return MyLinkedList(firstNode, lastNode, size)
    }

    inline fun<R> mapNotNullMyLinkedList(transform: (T)-> R?): MyLinkedList<R> {
        val iterator = normalIterator()

        val newFirst: Node<R>

        while (true) {
            if (iterator.hasNext()) {
                val transformed: R = transform(iterator.next()) ?: continue

                newFirst = Node(transformed)
                break
            } else {
                return MyLinkedList()
            }
        }

        var newLast = newFirst
        var size = 1

        while (iterator.hasNext()) {
            val transformed: R = transform(iterator.next()) ?: continue

            Node(transformed).also { newNode ->
                newLast.next = newNode
                newLast = newNode
            }
            size++
        }
        return MyLinkedList(newFirst, newLast, size)
    }

    override fun contains(element: T): Boolean {
        normalIterator().forEach { elem ->
            if (elem == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val fastContains = containsInConstantTime()

        for (element in elements) {
            if (fastContains(element)) return true
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

    fun normalListIterator() = object: ListIterator<T> by toList().listIterator() {}

    override fun listIterator() = object: MutableListIterator<T> {

        private var iterator = Node.iterator(first)
        var currentIndex = 0
            private set

        override fun hasPrevious() = iterator.index > 0u

        override fun nextIndex(): Int = iterator.index.toInt() + 1

        /**Warning: O(n) performance.*/
        override fun previous(): T {
            iterator = Node.iterator(first, iterator.index.toInt() - 2)
            return iterator.next().elem
        }

        override fun previousIndex(): Int = iterator.index.toInt() - 1

        override fun add(element: T) {
            iterator.add(element)
            first = iterator.first
        }

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): T {
            return iterator.next().elem
        }

        override fun remove() {
            iterator.remove()
        }

        override fun set(element: T) {
            iterator.currentNode
        }
    }

    override fun listIterator(index: Int): MutableListIterator<T> = listIterator().apply {
        repeat(index){ next() }
    }
    
    override fun subList(fromIndex: Int, toIndex: Int): MyLinkedList<T> {
        val cant = toIndex - fromIndex
        if(cant <= 0) return MyLinkedList()

        val first = getNode(fromIndex)
        var last = first
        for (i in 0 until cant) {
            last = last.next ?: throw IndexOutOfBoundsException()
        }
        return MyLinkedList(first, last, toIndex - fromIndex)
    }

    override fun toString() = StringBuilder().run {
        val iter = this@MyLinkedList.normalIterator()

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

    private fun getNode(index: Int): Node<T> {
        if(index == size-1) {
            return last ?: throw size outOfBoundsOf index
        }
        var currentNode = first ?: throw size outOfBoundsOf index
        for (i in 0 until index) {
            currentNode = currentNode.next ?: throw size outOfBoundsOf index
        }
        return currentNode
    }

    private fun getNodeOrNull(index: Int): Node<T>? {
        if(index == size-1) {
            return last
        }
        var currentNode = first ?: return null
        for (i in 0 until index) {
            currentNode = currentNode.next ?: return null
        }
        return currentNode
    }

    private inline fun _addAll(elements: Collection<T>, onNotEmpty: (first: Node<T>, last: Node<T>) -> Unit): Boolean {
        synchronized(this) {
            val iter =
                if (elements is MyLinkedList) elements.normalIterator()
                else elements.iterator()

            val (firstNode, lastNode) = Node.createChain(iter) ?: return false

            if (isEmpty()) {
                first = firstNode
                last = lastNode
                size = elements.size
            } else {
                onNotEmpty(firstNode, lastNode)
                size += elements.size
            }
            return true
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        _addAll(elements) { firstNode, lastNode ->
            val prevNode = first!![index]
            if (prevNode?.next == null) {
                addAllCallBack(firstNode, lastNode)
            } else {
                val nextNode = prevNode.next!!
                prevNode += firstNode
                lastNode += nextNode
            }
        }

    private inline fun addAllCallBack(firstNode: Node<T>, lastNode: Node<T>) {
        last!!.next = firstNode
        last = lastNode
    }

    override fun addAll(elements: Collection<T>): Boolean =
        _addAll(elements, ::addAllCallBack)

    override fun remove(element: T): Boolean {
        val iter = iterator()

        while (iter.hasNext()) {
            if (iter.next() == element) {
                iter.remove()
                return true
            }
        }
        return false
    }
    
    override fun removeAll(elements: Collection<T>): Boolean =
        removeIf(elements, elements.containsInConstantTime())

    override fun retainAll(elements: Collection<T>): Boolean =
        removeIf(elements, elements.notContainsInConstantTime())

    inline fun removeIf(elements: Collection<T>, condition: (T) -> Boolean): Boolean {
        if (elements.isEmpty()) return false
        var somethingHasBeenRemoved = false

        val iter = iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            if ( condition(item) ) {
                iter.remove()
                somethingHasBeenRemoved = true
            }
        }
        return somethingHasBeenRemoved
    }

    fun forEach(consumer: (T) -> Unit) {
        first?.forEach(consumer)
    }

    fun onEach(consumer: (T) -> Unit) = apply {
        first?.forEach(consumer)
    }

    private inline fun<T> T.log(msj: String? = null) = apply {
        Log.i("MyLinkedList", "${if (msj != null) "$msj: " else ""}${toString()}")
    }

}


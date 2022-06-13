package com.example.taskscheduler.util.dataStructures

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Node<T>(
    var elem: T,
    var next: Node<T>? = null
) {
    operator fun get(index: Int): Node<T>? {
        if (index < 1) return this

        var it = this

        for (i in 0 until index){
            it = it.next ?: return null
        }
        return it
    }

    inline operator fun plusAssign(other: Node<T>?) { next = other }

    /** Helps the garbage collector to destroy an unused chain of nodes.*/
    fun destroyChain() {
        CoroutineScope(Dispatchers.Default).launch {
            var currentNode = this@Node
            while (true) {
                val nextNode = currentNode.next ?: break
                currentNode.next = null
                currentNode = nextNode
            }
        }
    }

    companion object {
        class NodeIterator<T>(firstNode: Node<T>?, initNode: Int = 0): MutableIterator<Node<T>> {
            var first = firstNode
                private set
            var currentNode = firstNode
                private set
            var prevPrevNode: Node<T>? = null
                private set
            var index = 0u
                private set
            var isLastRemoved = true
                private set

            init {
                //initNode ?: return  //'return' is not allowed here
                repeat(initNode) { next() }
            }

            override fun hasNext() = currentNode != null

            override fun next(): Node<T> {
                if (index == 2u) {
                    prevPrevNode = first
                } else if (index > 2u && !isLastRemoved) {
                    prevPrevNode = prevPrevNode?.next
                }

                val res = currentNode!!
                currentNode = currentNode!!.next

                index++
                isLastRemoved = false
                return res
            }

            override fun remove() {
                if (isLastRemoved) return

                val removedNode: Node<T>?

                if (index == 1u) {
                    removedNode = first
                    first = removedNode?.next
                    index--
                    isLastRemoved = true
                } else if (index > 1u) {
                    removedNode = prevPrevNode!!.next
                    prevPrevNode!! += removedNode?.next
                    index--
                    isLastRemoved = true
                } else {
                    removedNode = null
                }
                removedNode?.next = null
            }

            fun add(element: T) {
                if (prevPrevNode == null) {
                    first = Node(element, first)
                } else {
                    prevPrevNode!!.next!!.next = Node(element, currentNode)
                }

                index++
            }

        }

        fun<T> iterator(firstNode: Node<T>?, initNode: Int = 0) = NodeIterator(firstNode, initNode)

        inline fun<T> createChain(iterable: Iterable<T>): Pair<Node<T>, Node<T>>? {
            return createChain(iterable.iterator())
        }

        inline fun<T, R> createTransformedChain(
            iterable: Iterable<T>, transform: (T)-> R
        ): Pair<Node<R>, Node<R>>? {
            return createTransformedChain(iterable.iterator(), transform)
        }

        inline fun<T> createChainIf(
            iterable: Iterable<T>, condition: (T) -> Boolean
        ): Triple<Node<T>, Node<T>, Int>? {
            return createChainIf(iterable.iterator(), condition)
        }


        fun<T> createChain(iterator: Iterator<T>): Pair<Node<T>, Node<T>>? {
            val first = if (iterator.hasNext()) Node(iterator.next()) else return null
            var last = first

            while (iterator.hasNext()) {
                Node(iterator.next()).also { newNode ->
                    last.next = newNode
                    last = newNode
                }
            }

            return first to last
        }

//        inline fun add(generator: ()-> T?) {
//            while (true) {
//                add(generator() ?: break)
//            }
//        }


        inline fun<T> createChain(generator: ()-> T?): Triple<Node<T>, Node<T>, Int>? {

            val first: Node<T> = Node(generator() ?: return null)
            var last = first
            var size = 1

            while (true) {
                val newItem: T = generator() ?: break

                Node(newItem).also { newNode: Node<T> ->
                    last.next = newNode
                    last = newNode
                }
                size++
            }

            return Triple(first, last, size)
        }

        inline fun<T> createChainIf(
            iterator: Iterator<T>, condition: (T) -> Boolean
        ): Triple<Node<T>, Node<T>, Int>? {

            val newFirst: Node<T>

            while (true) {
                if (iterator.hasNext()) {
                    val candidateElem: T = iterator.next()

                    if (! condition(candidateElem)) continue

                    newFirst = Node(candidateElem)
                    break
                } else {
                    return null
                }
            }

            var newLast = newFirst
            var size = 1

            while (iterator.hasNext()) {
                val candidateElem: T = iterator.next()

                if (! condition(candidateElem)) continue

                Node(candidateElem).also { newNode ->
                    newLast.next = newNode
                    newLast = newNode
                }
                size++
            }

            return Triple(newFirst, newLast, size)
        }

        inline fun<T, R> createTransformedChain(iterator: Iterator<T>, transform: (T)-> R): Pair<Node<R>, Node<R>>? {
            val first = if (iterator.hasNext()) Node(transform(iterator.next())) else return null
            var last = first

            while (iterator.hasNext()) {
                Node(transform(iterator.next())).also { newNode ->
                    last.next = newNode
                    last = newNode
                }
            }

            return first to last
        }
    }
}


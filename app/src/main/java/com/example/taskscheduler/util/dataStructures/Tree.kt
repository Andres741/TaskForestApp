package com.example.taskscheduler.util.dataStructures

/**
 * An abstract class that is capable of transform in an n-tree the classes that implement it,
 * therefore the root is "this: ITree".
 */
abstract class ACTree {

    private val children = object {

        var first: Node? = null
        var last = first
        var size = 0

        fun isEmpty() = size == 0
        fun isNotEmpty() = !isEmpty()

        operator fun get(index: Int): ACTree {
            return getNode(index).elem
        }

        fun add(value: ACTree) {
            if (first == null) {
                Node(value).apply {
                    first = this
                    last = this
                }
                return
            }
            last!!.next = Node(value)
            last = last!!.next!!
            size++
        }

        fun set(index: Int, value: ACTree) {
            getNode(index).elem = value
        }

        fun removeAt(index: Int) {
            val prevNode = getNode(index - 1)
            val postNode = getNode(index + 1)
            prevNode.next = postNode
        }

        operator fun iterator() = object: Iterator<ACTree> {
            var currentNode = first

            override fun hasNext(): Boolean {
                currentNode?.apply {
                    return next != null
                }
                return false
            }

            override fun next(): ACTree {
                val res = currentNode!!.elem
                currentNode = currentNode!!.next
                return res
            }
        }

        private fun getNode(index: Int): Node {
            var currentNode = first ?: throw IndexOutOfBoundsException()
            for (i in 0 until index) {
                currentNode = currentNode.next ?: throw IndexOutOfBoundsException()
            }
            return currentNode
        }

        inner class Node(
            var elem: ACTree,
            var next: Node? = null
        )
    }

//    val childrenIterator: Iterator<ITree<T>>

    val numChildren get() = children.size

    val numAllChildren: Int get() {
        var num = numChildren

        for (child in children) {
            num += child.numAllChildren
        }
        return num
    }

    val hasChildren get() = children.isNotEmpty()

    operator fun get(index: Int) = children[index]

    fun remove(index: Int) = children.removeAt(index)

    fun setChild(index: Int, value: ACTree) = children.set(index, value)

    fun addChild(value: ACTree) = children.add(value)

    fun contains(value: ACTree): Boolean {
//        if (this == value) return true
//
//        for (child in children) {
//            if (child.contains(value)) return true
//        }
//        return false
        return contains(value){
            this == value
        }
    }

    fun contains(value: ACTree, eval: ACTree.(ACTree) -> Boolean): Boolean {
        if (eval(value)) return true

        for (child in children) {
            if (child.contains(value)) return true
        }

        return false
    }

    operator fun iterator() = toList().iterator()

    fun toList(preorder: Boolean = true) = if (preorder) {
        preorder(this, mutableListOf<ACTree>()).toList()
    } else {
        postorder(this, mutableListOf<ACTree>()).toList()
    }


    private fun preorder(root: ACTree, list: MutableList<ACTree>): MutableList<ACTree> {
        list.add(root)

        for (elem in list) {
            preorder(elem, list)
        }
        return list
    }

    private fun postorder(root: ACTree, list: MutableList<ACTree>): MutableList<ACTree> {
        for (elem in list) {
            preorder(elem, list)
        }

        list.add(root)

        return list
    }
}

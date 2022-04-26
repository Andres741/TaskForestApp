package com.example.taskscheduler.util.dataStructures

import io.reactivex.internal.operators.flowable.FlowableDelaySubscriptionOther
import timber.log.Timber

open class Tree<T>(
    var value: T
) {
    protected open val _children = LinkedList<Tree<T>>()
    open val children: List<Tree<T>> get() = _children
//    val childrenIterator: Iterator<ITree<T>>

    val numChildren get() = _children.size

    val numAllChildren: Int get() {
        var num = numChildren

        for (child in _children) {
            num += child.numAllChildren
        }
        return num
    }

    val hasChildren get() = _children.isNotEmpty()

    open operator fun get(index: Int) = _children[index]

    fun removeChildAt(index: Int) = _children.removeAt(index)

    fun removeChild() = _children.remove()

    open fun setChild(index: Int, value: T) = _children.set(index, buildSelf(value))

    open fun addChild(value: T) = _children.add(buildSelf(value))

    open fun addChildren(values: Iterable<T>) {
        _children.addAll(Iterable {
            return@Iterable object: Iterator<Tree<T>> {

                val iter = values.iterator()

                override fun hasNext() = iter.hasNext()

                override fun next() = buildSelf(iter.next())
            }
        })
    }

    fun contains(value: T): Boolean {
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

    fun contains(value: T, eval: T.(T) -> Boolean): Boolean {

        if (this.value.eval(value)) return true

        for (child in _children) {
            if(child.contains(value, eval)) return true
        }
        return false
    }

    operator fun iterator() = toLinkedList().iterator()


    open fun toLinkedList(preorder: Boolean = true) = if (preorder) preorder() else postorder()


    fun toList(preorder: Boolean = true): List<T> = toLinkedList(preorder)

    protected fun preorder(root: Tree<T> = this, list: LinkedList<T> = LinkedList()): LinkedList<T> {
        list.add(root.value)

        for (elem in root._children) {
            preorder(elem, list)
        }
        return list
    }

    protected fun postorder(root: Tree<T> = this, list: LinkedList<T> = LinkedList()): LinkedList<T> {
        for (elem in root._children) {
            postorder(elem, list)
        }
        list.add(root.value)

        return list
    }
    protected open fun buildSelf(value: T): Tree<T>{
        return Tree(value)
    }
}

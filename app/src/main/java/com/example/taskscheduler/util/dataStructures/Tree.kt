package com.example.taskscheduler.util.dataStructures

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow


/**
 * An implementation of the data structure general tree with an indeterminate number of children.
 */
open class Tree<T>(
    var value: T
) {
    protected open val _children = MyLinkedList<Tree<T>>()
    /**A list with the children of the list.*/
    open val children: List<Tree<T>> get() = _children
//    val childrenIterator: Iterator<ITree<T>>
    open val childrenIter get() = _children.asIterable()

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

    fun removeChild() = _children.removeFirst()

    open fun setChild(index: Int, value: T) = _children.set(index, buildChild(value))

    open fun addChild(value: T): Tree<T> {
        return buildChild(value).apply(_children::add)
    }

    open fun addChildren(values: Iterable<T>) {
        _children.addAll( Iterable {
            return@Iterable object: Iterator<Tree<T>> {

                val iter = values.iterator()

                override fun hasNext() = iter.hasNext()

                override fun next() = buildChild(iter.next())
            }
        })
    }

    infix fun contains(value: T): Boolean {
        if (this.value == value) return true

        for (child in _children.normalIterator()) {
            if (child contains value) return true
        }
        return false
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

    protected fun preorder(root: Tree<T> = this, list: MyLinkedList<T> = MyLinkedList()): MyLinkedList<T> {
        list.add(root.value)

        for (elem in root._children.normalIterator()) {
            preorder(elem, list)
        }
        return list
    }

    protected fun postorder(root: Tree<T> = this, list: MyLinkedList<T> = MyLinkedList()): MyLinkedList<T> {
        for (elem in root._children.normalIterator()) {
            postorder(elem, list)
        }
        list.add(root.value)

        return list
    }

    fun forEach(operation: (T) -> Unit) {
        operation(value)
        _children.normalIterator().forEach { child ->
            child.forEach(operation)
        }
    }

    open fun forEachTree(operation: (Tree<T>) -> Unit) {
        operation(this)
        _children.forEach { child ->
            child.forEachTree(operation)
        }
    }

    fun asFlow(): Flow<T> {
        suspend fun FlowCollector<T>.helper(tree: Tree<T>) {
            emit(tree.value)
            tree._children.forEach { child ->
                helper(child)
            }
        }

        return flow {
            helper(this@Tree)
        }
    }

    protected open fun buildChild(value: T) = Tree(value)
}

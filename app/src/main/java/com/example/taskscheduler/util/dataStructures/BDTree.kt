package com.example.taskscheduler.util.dataStructures

/**
 * Bidirectional tree, is an implementation of the data structure general tree that extends from Tree, but each
 * node are linked to its father.
 */
open class BDTree<T>(value: T): Tree<T>(value) {

    var father: BDTree<T>? = null
    /**Determinate if is the super father, that is, he has no father.*/
    val isSuperFather get() = father == null

    /**The level of the tree where this node are. The super father is in the level 0.*/
    val level: Int
        get() = getLevelHelper()

    /**The father of all the nodes of the tree.*/
    val superFather: BDTree<T>
        get() = getSuperFatherHelper()


//    override val _children = LinkedList<BDTree<T>>()
    private val bdtChildren = _children as MyLinkedList<BDTree<T>>
    override val children: List<BDTree<T>> get() = bdtChildren

    override val childrenIter get() = bdtChildren.asIterable()

    override operator fun get(index: Int) = super.get(index) as BDTree

    private tailrec fun getLevelHelper(bdTree: BDTree<T> = this, res: Int = 0): Int {
        val superFather = bdTree.father
        return if (superFather == null) res else getLevelHelper(superFather, res+1)
    }

    private tailrec fun getSuperFatherHelper(bdTree: BDTree<T> = this): BDTree<T> {
        val superFather = bdTree.father
        return if (superFather == null ) bdTree else getSuperFatherHelper(superFather)
    }

    override fun addChild(value: T): BDTree<T> {
        return buildChild(value).apply(_children::add)
    }

    /**Transforms the whole tree into a LinkedList, including the higher level nodes.*/
    fun toLinkedListAll(preorder: Boolean = true): MyLinkedList<T> {
        return if (preorder) preorder(superFather) else postorder(superFather)
    }

    fun <R> map(transform: (T) -> R) = BDTree<R>(transform(value)).also { other ->
        bdtChildren.normalIterator().forEach { thisChild ->
            val mapped = other.addChild(transform(thisChild.value))
            thisChild.mapTo(transform, mapped)
        }
    }

    private fun <R> mapTo(transform: (T) -> R, other: BDTree<R>) {
        bdtChildren.normalIterator().forEach { thisChild ->
            val mapped = other.addChild(transform(thisChild.value))
            thisChild.mapTo(transform, mapped)
        }
    }

    fun forEachBDTree(operation: (BDTree<T>) -> Unit) {
        operation(this)
        bdtChildren.normalIterator().forEach { child ->
            child.forEachBDTree(operation)
        }
    }


    /**Transforms the whole tree into a List, including the higher level nodes.*/
    fun toListAll(preorder: Boolean = true): List<T> = toLinkedListAll(preorder)

    override fun buildChild(value: T) = BDTree(value).also { newChild ->
        newChild.father = this // I am your father
    }
}

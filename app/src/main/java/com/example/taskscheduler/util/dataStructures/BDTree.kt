package com.example.taskscheduler.util.dataStructures

/**
 * Bidirectional tree, is an implementation of the data structure general tree that extends from Tree, but each
 * node are linked to its father.
 */
class BDTree<T>(value: T) : Tree<T>(value) {

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
    private val bdtChildren = _children as LinkedList<BDTree<T>>
    override val children: List<BDTree<T>> get() = bdtChildren

    override fun buildSelf(value: T) = BDTree(value).also { newChild->
        newChild.father = this // I am your father
    }

    override operator fun get(index: Int) = super.get(index) as BDTree

    private tailrec fun getLevelHelper(bdTree: BDTree<T> = this, res: Int = 0): Int {
        val father = bdTree.father
        return if (father == null) res else getLevelHelper(father, res+1)
    }

    private tailrec fun getSuperFatherHelper(bdTree: BDTree<T> = this): BDTree<T> {
        val father = bdTree.father
        return if (father == null ) bdTree else getSuperFatherHelper(father)
    }

    /**Transforms the whole tree into a LinkedList, including the higher level nodes.*/
    fun toLinkedListAll(preorder: Boolean = true): LinkedList<T> {
        return if (preorder) preorder(superFather) else postorder(superFather)
    }

    /**Transforms the whole tree into a List, including the higher level nodes.*/
    fun toListAll(preorder: Boolean = true): List<T> = toLinkedListAll(preorder)
}

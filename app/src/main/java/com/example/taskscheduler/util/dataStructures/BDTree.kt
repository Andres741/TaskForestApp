package com.example.taskscheduler.util.dataStructures

class BDTree<T>(value: T) : Tree<T>(value) {
    var father: BDTree<T>? = null
    /**Determinate if is the super father, that is, he has no father.*/
    val isSuperFather get() = father == null

    val level: Int
        get() = getLevelHelper()

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

    fun toLinkedListAll(preorder: Boolean = true): LinkedList<T> {
        return if (preorder) preorder(superFather) else postorder(superFather)
    }

    fun toListAll(preorder: Boolean = true): List<T> = toLinkedListAll(preorder)
}

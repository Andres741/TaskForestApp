package com.example.taskscheduler.util.dataStructures

import com.example.taskscheduler.data.sources.local.taskTree.toModel
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.toTrees
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import junit.framework.TestCase

class BDTreeTest : TestCase() {

    private val taskModels by AsyncLazy { taskModelTree }

    /**Main tree*/
    lateinit var bdTree: BDTree<String>

    lateinit var subTree: BDTree<String>
    lateinit var subSubTree: BDTree<String>

    /**List with the same elements as bdTree*/
    lateinit var list: MutableList<String>
    /**Sub trees of the main tree*/
    val numSub = 3
    /**Subtrees of subtrees of the main tree*/
    val numSubSub = 3

    fun printNumSub() = println("numSub: $numSub, numSubSub: $numSubSub ")

    val sub = 1
    val subSub = 1

    fun printSub() = println("sub: $sub, subSub: $subSub ")


    val putInPL: String.()-> String = {
        list.add(this)
        this
    }

    fun buildTree() {
        list = mutableListOf()

        bdTree = BDTree("top".putInPL())
        for (i in 0 until numSub) {
            bdTree.addChild("sub $i".putInPL())
        }
        for (i in 0 until numSubSub) {
            for (j in 0 until 3) {
                bdTree[i].addChild("sub $i-$j".putInPL())
            }
        }
        subTree = bdTree[sub]
        subSubTree = subTree[subSub]

    }
    public override fun setUp() {
        super.setUp()
        println("\n/-----------------------------------------\\\n")
        buildTree()
    }

    public override fun tearDown() {
        println("\n\\-----------------------------------------/\n")
    }


    fun testGetNumChildren() {
        assertEquals(3, bdTree.numChildren)
    }

    fun testGetNumAllChildren() {
        assertEquals(list.size-1, bdTree.numAllChildren)
    }

    fun testGetHasChildren() {
        assert(bdTree.hasChildren)
    }

    fun testRemoveAt() {
        bdTree.removeChildAt(1)

        bdTree.toLinkedListAll().apply(::println)
    }

    fun testRemove() {
        printSub()
        bdTree.removeChild()
        bdTree.toLinkedListAll().apply(::println)
        buildTree()
        subTree.removeChild()
        subTree.toLinkedListAll().apply(::println)
        buildTree()
        subSubTree.removeChild()
        bdTree.toLinkedListAll().apply(::println)
    }

    fun testSetChild() {
        bdTree[0].setChild(2, "NEW 0-2")
        bdTree[1].setChild(1, "NEW 1-1")

        bdTree.toLinkedListAll().apply(::println)
    }

    fun testAddChild() {
        Tree("top")
        bdTree.addChild("sub 0")
        bdTree.addChild("sub 1")
        bdTree.addChild("sub 2")

        for (i in 0 until numSub) {
            for (j in 0 until numSubSub) {
                bdTree[i].addChild("sub $i-$j")
            }
        }
    }

    fun testAddChildren() {
        bdTree = BDTree("top")
        bdTree.addChildren(listOf("sub 1" ,"sub 2" ,"sub 3"))
    }

    fun testTestContains() {
        assert(bdTree.contains("top"))
        assert(bdTree.contains("sub 0"))
        assert(bdTree.contains("sub 1-2"))
        assert(bdTree.contains("sub 0-0"))
        assert(bdTree.contains("sub 1"))
        assert(bdTree.contains("sub 2-2"))
        assertEquals(bdTree.contains("bj"), false)
        assertEquals(bdTree.contains("vgyuvcgy"), false)
    }

    fun testIterator() {
        val iterator = bdTree.iterator()

        var expected = 0
        var actual = 0
        while (iterator.hasNext()) {
            print("${iterator.next()}, ")
            actual++
        }
        println("\n------------------------------")
        for (elem in list) {
            print("$elem, ")
            expected++
        }

        assertEquals(expected, actual)
    }

    fun testGetLevel() {
        assertEquals("bdTree.level: ",0, bdTree.level)
        assertEquals("subTree.level: ",1, subTree.level)
        assertEquals("subSubTree.level: ",2, subSubTree.level)
    }

    fun testTestGetChildren() {
        assertEquals(bdTree.value, "top")
        for (i in 0 until numSub) {
            assertEquals(bdTree.children[i].value, "sub $i".also(::println))
        }
        println("----------------------------------------")
        for (i in 0 until numSubSub) {
            for (j in 0 until 3) {
                assertEquals(bdTree.children[i].children[j].value, "sub $i-$j".apply(::println))
            }
        }
    }

    /**This test indirectly also test the attribute superFather.*/
    fun testToLinkedListAll() {
        println("Expected:")
        val expected = bdTree.toLinkedList().apply(::println)
        println("--------------------------------------")
        println("Actual")
        val actual = subSubTree.toLinkedListAll().apply(::println)
        println("--------------------------------------")
        println("toLinkedList()")
        subSubTree.toLinkedList().apply(::println)

        assertEquals(expected, actual)
    }

    fun testMap() {
        val original = MyLinkedList<TaskModel>()
        val mapped = HashSet<TaskModel>()

        "---Original trees---".bigLog()

        val trees = taskModels.toTrees().onEach { rootTree ->
            "New tree".log()
            rootTree.forEachBDTree { branch ->
                "${branch.value.also(original::add)}".log("  value")
                "${branch.children.map { it.value.title }}".log("    children")
            }
        }

        "---Mapped trees---".bigLog()

        trees.forEach { tree ->
            "Mapped tree".log()
            tree.map(TaskModel::toEntity).apply {
                forEachBDTree { branch ->
                    "${branch.value}".log("  Value mapped")
                    "${branch.toModel().also(mapped::add) }".log("  To model    ")
                    "${branch.childrenIter.map { child -> child.value.title }}".log("    children mapped")
                }
            }
        }

        assertEquals(original.size, mapped.size)
        original.normalIterator().forEach { originalTask ->
            assert(mapped.remove(originalTask))
        }
    }
}

private fun <T> T.log(msj: Any? = null) = apply {
    println("${if (msj != null) "$msj: " else ""}${toString()}")
}
private fun <T> T.bigLog(msj: Any? = null) = apply {
    "\n$this\n".uppercase().log(msj)
}
private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
    "$msj:".uppercase().log()
    this.iterator().hasNext().ifFalse {
        "  Collection is empty".log()
        return@apply
    }
    forEachIndexed { index, elem ->
        elem.log("  $index")
    }
}

package com.example.taskscheduler.domain.models

import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import junit.framework.TestCase

class TaskModelTest : TestCase() {


    private val taskModels by AsyncLazy { taskModelTree }


    public override fun setUp() {
        super.setUp()
        "\n/-----------------------------------------\\\n".log()

    }

    public override fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }

    fun testToTrees() {
        taskModels.logList("No tree")

        val trees = taskModels.toTrees()
        trees.size.log("trees.size")
        "trees".bigLog()
        val titleSet = mutableSetOf<String>()
        trees.forEach { tree ->
            val treeList = tree.toLinkedListAll()
            treeList.logList(tree.value.title)
            titleSet.addAll(treeList.map(TaskModel::title))
        }
        assertEquals(taskModels.size, titleSet.size)
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

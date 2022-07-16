package com.example.taskscheduler.data.sources.local.taskTree

import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.toTrees
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import junit.framework.TestCase

class TaskForestTest : TestCase() {

    private val taskModels by AsyncLazy { taskModelTree }


    public override fun setUp() {
        super.setUp()
        "\n/-----------------------------------------\\\n".log()

    }

    public override fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }

    fun test_constructor() {
        val forest = TaskForest(taskModels.logList("task Models"))

        "forest".bigLog()
        forest.log()
//        forest.toList().logList("forest.toList")
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

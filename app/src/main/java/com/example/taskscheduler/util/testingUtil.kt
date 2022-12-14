package com.example.taskscheduler.util

import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.toTaskTitle

val taskModelTree: List<TaskModel> get() = mutableListOf<TaskModel>().apply {
    addAll(taskModelTree_a)
    addAll(taskModelTree_b)
    addAll(taskModelTree_c)
    addAll(taskModelTree_x)
    addAll(taskModelTree_y)
    addAll(taskModelTree_z)
}

val taskModelTree_a: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "a", type = "a", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = listOf(
            "aa".toTaskTitle()
        )
    ),
    TaskModel(
        title = "aa", type = "a", description = "", adviseDate = null,
        superTask = "a".toTaskTitle(), subTasks = listOf(
            "aaa".toTaskTitle()
        )
    ),
).apply {
    addAll(taskModelTree_aa)
}

val taskModelTree_aa: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "aaa", type = "a", description = "", adviseDate = null,
        superTask = "aa".toTaskTitle(), subTasks = emptyList()
    ),
)

val taskModelTree_b get() = listOf(
    //bs
    TaskModel(
        title = "b", type = "b", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = listOf(
            "bb", "bbb"
        ).toTaskTitle()
    ),
    TaskModel(
        title = "bb", type = "b", description = "", adviseDate = null,
        superTask = "b".toTaskTitle(), subTasks = emptyList()
    ),
    TaskModel(
        title = "bbb", type = "b", description = "", adviseDate = null,
        superTask = "b".toTaskTitle(), subTasks = emptyList()
    ),
)

val taskModelTree_c: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "c", type = "c", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = listOf(
            "cc"
        ).toTaskTitle()
    ),
    TaskModel(
        title = "cc", type = "c", description = "", adviseDate = null,
        superTask = "c".toTaskTitle(), subTasks = listOf(
            "ccc", "cccc", "ccccc"
        ).toTaskTitle()
    ),
).apply {
    addAll(taskModelTree_cc)
}

val taskModelTree_cc: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "ccc", type = "c", description = "", adviseDate = null,
        superTask = "cc".toTaskTitle(), subTasks = emptyList()
    ),
    TaskModel(
        title = "cccc", type = "c", description = "", adviseDate = null,
        superTask = "cc".toTaskTitle(), subTasks = listOf("ccccccc").toTaskTitle()
    ),
    TaskModel(
        title = "ccccc", type = "c", description = "", adviseDate = null,
        superTask = "cc".toTaskTitle(), subTasks = listOf("cccccc").toTaskTitle()
    ),
).apply {
    addAll(taskModelTree_ccccc)
    addAll(taskModelTree_cccccc)
}

val taskModelTree_ccccc: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "cccccc", type = "c", description = "", adviseDate = null,
        superTask = "ccccc".toTaskTitle(), subTasks = emptyList()
    ),
)

val taskModelTree_cccccc: List<TaskModel> get() = mutableListOf(
    TaskModel(
        title = "ccccccc", type = "c", description = "", adviseDate = null,
        superTask = "cccc".toTaskTitle(), subTasks = emptyList()
    ),
)

val taskModelTree_x get() = listOf(
    TaskModel(
        title = "x", type = "x", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = emptyList()
    ),
    TaskModel(
        title = "xx", type = "x", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = emptyList()
    ),
)
val taskModelTree_y get() = listOf(
    TaskModel(
        title = "y", type = "y", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = emptyList()
    ),
)
val taskModelTree_z get() = listOf(
    TaskModel(
        title = "z", type = "z", description = "", adviseDate = null,
        superTask = "".toTaskTitle(), subTasks = emptyList()
    ),
)

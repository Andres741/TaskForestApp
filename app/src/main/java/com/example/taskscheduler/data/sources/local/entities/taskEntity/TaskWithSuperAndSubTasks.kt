package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.taskscheduler.domain.models.TaskModel

data class TaskWithSuperAndSubTasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = TITLE_ID,
        entityColumn = SUPER_TASKa
    )
    val subTaskEntities: List<SubTaskEntity>,
    @Relation(
        parentColumn = TITLE_ID,
        entityColumn = SUB_TASK_ID
    )
    val superTaskEntity: SubTaskEntity?
) {
    private val superTask: String get() = superTaskEntity!!.superTask
    private val subTasks: List<String> get() = subTaskEntities.map { it.subTask }
    val hasSubTasks: Boolean get() = subTaskEntities.isNotEmpty()
    val hasSuperTask: Boolean get() = superTaskEntity!!.hasSuperTask

    fun toModel() = TaskModel(this)
}

fun Iterable<TaskWithSuperAndSubTasks>.toModel(): List<TaskModel> = map(TaskWithSuperAndSubTasks::toModel)

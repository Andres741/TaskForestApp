package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithSuperTask(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = TITLE_ID,
        entityColumn = SUB_TASK_ID
    )
    val superTaskEntity: SubTaskEntity
)

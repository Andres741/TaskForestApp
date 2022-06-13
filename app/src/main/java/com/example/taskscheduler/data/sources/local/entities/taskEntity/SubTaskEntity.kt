package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val SUBTASK_TABLE = "subtaskTable"
const val SUB_TASK_ID = "subTaskID"
const val SUPER_TASKa = "superTask_a"

@Entity(tableName = SUBTASK_TABLE)
data class SubTaskEntity(
    @ColumnInfo(name = SUPER_TASKa) //A task can be the super task of many tasks
    val superTask: String,
    @ColumnInfo(name = SUB_TASK_ID) @PrimaryKey //A task only can be a sub task once
    val subTask: String,
)


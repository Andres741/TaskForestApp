package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = subtaskTable)
data class SubTaskEntity(
    @ColumnInfo(name = subTaskID) @PrimaryKey //A task only can be a sub task once
    val subTask: String,
    @ColumnInfo(name = superTask_a) //A task can be the super task of many tasks
    val superTask: String,
)

const val subtaskTable = "subtaskTable"
const val subTaskID = "subTaskID"
const val superTask_a = "superTask_a"

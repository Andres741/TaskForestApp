package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtask_table")
data class SubTaskEntity(
    @ColumnInfo(name = "superTask") //A task can be the super task of many tasks
    val superTask: String,
    @ColumnInfo(name = "subTaskID") @PrimaryKey //A task only can be a sub task once
    val subTask: String,
)
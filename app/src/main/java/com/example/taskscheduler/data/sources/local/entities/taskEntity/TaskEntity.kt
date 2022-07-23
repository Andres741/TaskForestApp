package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TASK_TABLE = "taskTable"
const val TITLE_ID = "titleID"
const val TYPEa = "type"
const val DESCRIPTIONa = "description_a"
const val IS_DONEa = "isDone_a"
const val DATEa = "date_a"
const val ADVISE_DATEa = "adviseDate_a"

@Entity(tableName = TASK_TABLE)
data class TaskEntity(
    @ColumnInfo(name = TITLE_ID) @PrimaryKey
    val title: String,
    @ColumnInfo(name = TYPEa)
    val type: String,
    @ColumnInfo(name = DESCRIPTIONa)
    val description: String,
    @ColumnInfo(name = IS_DONEa)
    val isDone: Boolean,
    @ColumnInfo(name = DATEa)
    val date: Long,
    @ColumnInfo(name = ADVISE_DATEa)
    val adviseDate: Long?,
)

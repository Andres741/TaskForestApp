package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.models.TaskModel

const val TASK_TABLE = "taskTable"
const val TITLE_ID = "titleID"
const val TYPEa = "type"
const val DESCRIPTIONa = "description_a"
const val IS_DONEa = "isDone_a"
const val DATEa = "date_a"

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
) {
    fun toModel() = TaskModel (this)
}

fun Iterable<TaskEntity>.toModel(): List<TaskModel> = map(TaskEntity::toModel)

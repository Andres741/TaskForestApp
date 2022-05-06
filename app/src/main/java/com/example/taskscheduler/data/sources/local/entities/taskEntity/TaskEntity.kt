package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.models.TaskModel

const val taskTable = "taskTable"
const val titleID = "titleID"
const val type_a = "type"
const val description_a = "description_a"
const val isDone_a = "isDone_a"

@Entity(tableName = taskTable)
data class TaskEntity(
    @ColumnInfo(name = titleID) @PrimaryKey
    val title: String,
    @ColumnInfo(name = type_a)
    val type: String,
    @ColumnInfo(name = description_a)
    val description: String,
    @ColumnInfo(name = isDone_a)
    val isDone: Boolean = false
) {
    fun toModel() = TaskModel (
        title = title, type = type, description = description, isDone = isDone
    )
}

fun Iterable<TaskEntity>.toModel(): List<TaskModel> = map(TaskEntity::toModel)

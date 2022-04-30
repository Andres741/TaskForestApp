package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.data.models.TaskModel

@Entity(tableName = taskTable)
data class TaskEntity(
    @ColumnInfo(name = titleID) @PrimaryKey
    val title: String,
    @ColumnInfo(name = type_a)
    val type: String,
    @ColumnInfo(name = description_a)
    val description: String,
) {
    fun toModel() = TaskModel(title, type, description)
}
const val taskTable = "taskTable"
const val titleID = "titleID"
const val type_a = "type_a"
const val description_a = "description_a"

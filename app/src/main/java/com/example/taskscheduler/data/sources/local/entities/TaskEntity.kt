package com.example.taskscheduler.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.data.models.TaskModel

@Entity(tableName = "task_table")
data class TaskEntity(
    @ColumnInfo(name = "titleID") @PrimaryKey
    val title: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "superTask") /**Keeps the key to one task*/
    val superTask: String = "",
    @ColumnInfo(name = "subTasks") /**Keeps the keys to others TaskEntity*/
    val subTasks: List<String> = emptyList(),
) {

    fun toModel(): TaskModel {
        return if (subTasks.isEmpty()) {
            TaskModel(title, type, description)
        } else {
            TODO("TaskDao needed to interact with the database.")
        }
    }
}



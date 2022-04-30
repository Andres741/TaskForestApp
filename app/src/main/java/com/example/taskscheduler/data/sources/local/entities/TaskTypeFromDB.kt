package com.example.taskscheduler.data.sources.local.entities

import androidx.room.ColumnInfo
import com.example.taskscheduler.data.sources.local.entities.taskEntity.type_a


data class TaskTypeFromDB(
    @ColumnInfo(name = type_a)
    val name: String,
    @ColumnInfo(name = countOfType_a)
    val multiplicity: Int,
)

const val countOfType_a = "countOfType_a"

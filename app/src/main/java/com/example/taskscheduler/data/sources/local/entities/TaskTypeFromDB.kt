package com.example.taskscheduler.data.sources.local.entities

import androidx.room.ColumnInfo


data class TaskTypeFromDB(
    @ColumnInfo(name = "type")
    val name: String,
    @ColumnInfo(name = "multiplicity")
    val multiplicity: Int,
)

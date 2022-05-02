package com.example.taskscheduler.data.sources.local.entities

import androidx.room.ColumnInfo
import com.example.taskscheduler.data.models.TaskTypeModel
import com.example.taskscheduler.data.sources.local.entities.taskEntity.type_a

const val countOfType_a = "countOfType_a"

data class TaskTypeFromDB(
    @ColumnInfo(name = type_a)
    val name: String,
    @ColumnInfo(name = countOfType_a)
    val multiplicity: Int,
) {
    fun toModel() = TaskTypeModel(name = name, multiplicity = multiplicity)  // the reverse operation does not have sense.
}

fun Iterable<TaskTypeFromDB>.toModel() = map(TaskTypeFromDB::toModel)

package com.example.taskscheduler.data.sources.local.entities

import androidx.room.ColumnInfo
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TYPEa

const val COUNT_OF_TYPEa = "countOfType_a"

data class TaskTypeFromDB(
    @ColumnInfo(name = TYPEa)
    val name: String,
    @ColumnInfo(name = COUNT_OF_TYPEa)
    val multiplicity: Int,
) {
    fun toModel() = TaskTypeModel(name = name, multiplicity = multiplicity)  // the reverse operation does not have sense.
}

fun Iterable<TaskTypeFromDB>.toModel() = map(TaskTypeFromDB::toModel)

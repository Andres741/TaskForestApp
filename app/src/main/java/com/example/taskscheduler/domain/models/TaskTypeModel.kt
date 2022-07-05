package com.example.taskscheduler.domain.models

import com.example.taskscheduler.util.dataStructures.IMultiplicityList
import com.example.taskscheduler.util.dataStructures.MultiplicityList

data class TaskTypeModel (
    val name: String,
    val multiplicity: Int,
) {
    fun toPair() = Pair(name, multiplicity)
}

fun Iterable<TaskTypeModel>.toPair() = map(TaskTypeModel::toPair)

fun Iterable<TaskTypeModel>.toMultiplicityList() = MultiplicityList(toPair())

fun IMultiplicityList<String>.allToModel() = elements.map {
        TaskTypeModel(it.key, it.value)
}

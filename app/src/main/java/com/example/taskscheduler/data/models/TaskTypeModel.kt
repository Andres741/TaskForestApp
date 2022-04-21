package com.example.taskscheduler.data.models

import com.example.taskscheduler.util.dataStructures.IMultiplicityList
import com.example.taskscheduler.util.dataStructures.MultiplicityList

data class TaskTypeModel (
    val name: String,
    val multiplicity: Int,
) {

}

fun IMultiplicityList<String>.allToModel() = _elements.map {
        TaskTypeModel(it.key, it.value)
}

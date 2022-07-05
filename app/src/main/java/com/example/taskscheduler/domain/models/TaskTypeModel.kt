package com.example.taskscheduler.domain.models

import com.example.taskscheduler.util.dataStructures.IMultiplicityList
import com.example.taskscheduler.util.dataStructures.MultiplicityList

data class TaskTypeModel (
    val name: String,
    val multiplicity: Int,
): ITaskTypeNameOwner {

    override val typeName: String get() = name

    fun toPair() = name to multiplicity

    fun Pair<String, Int>.toTaskTypeModel() = TaskTypeModel(first, second)
}

fun Iterable<TaskTypeModel>.toPair() = map(TaskTypeModel::toPair)

fun Iterable<TaskTypeModel>.toMultiplicityList() = MultiplicityList(toPair())

fun IMultiplicityList<String>.allToModel() = elements.map {
        TaskTypeModel(it.key, it.value)
}

sealed interface ITaskTypeNameOwner {
    val typeName: String

    fun toSimpleTaskTypeNameOwner() = SimpleTaskTypeNameOwner(this)
}

infix fun ITaskTypeNameOwner.equalsType(other: ITaskTypeNameOwner) = typeName == other.typeName
infix fun ITaskTypeNameOwner.notEqualsType(other: ITaskTypeNameOwner) = typeName != other.typeName

/**
 * Constructor should only be called in domain layer.
 */
@JvmInline
value class SimpleTaskTypeNameOwner (
    override val typeName: String
): ITaskTypeNameOwner {
    constructor(taskTypeNameOwner: ITaskTypeNameOwner): this(taskTypeNameOwner.typeName)
    override fun toSimpleTaskTypeNameOwner() = this
}

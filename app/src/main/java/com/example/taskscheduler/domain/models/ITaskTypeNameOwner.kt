package com.example.taskscheduler.domain.models

sealed interface ITaskTypeNameOwner {
    val typeName: String

    infix fun equalsType(other: ITaskTypeNameOwner) = typeName == other.typeName

    fun toSimpleTaskTypeNameOwner() = SimpleTaskTypeNameOwner(this)
}

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

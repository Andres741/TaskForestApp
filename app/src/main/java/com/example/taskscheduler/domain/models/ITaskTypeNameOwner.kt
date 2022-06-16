package com.example.taskscheduler.domain.models

sealed interface ITaskTypeNameOwner {
    val typeName: String

    fun equalsType(other: ITaskTypeNameOwner) = typeName == other.typeName

    fun toSimpleTaskTypeNameOwner() = SimpleTaskTypeNameOwner(this)
}

@JvmInline
value class SimpleTaskTypeNameOwner private constructor(
    override val typeName: String
): ITaskTypeNameOwner {
    constructor(taskTypeNameOwner: ITaskTypeNameOwner): this(taskTypeNameOwner.typeName)
    override fun toSimpleTaskTypeNameOwner() = this
}

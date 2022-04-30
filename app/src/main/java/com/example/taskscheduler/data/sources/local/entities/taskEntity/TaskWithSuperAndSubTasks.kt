package com.example.taskscheduler.data.sources.local.entities.taskEntity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.taskscheduler.data.models.TaskModel

data class TaskWithSuperAndSubTasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = titleID,
        entityColumn = superTask_a
    )
    val subTaskEntities: List<SubTaskEntity>,
    @Relation(
        parentColumn = titleID,
        entityColumn = subTaskID
    )
    val superTaskEntity: SubTaskEntity?
) {
    /* All of this produces the following error: error: Cannot find setter for field.

    val superTask: String by lazy { superTaskEntity?.superTask ?: "" }
    val subTasks: List<String> by lazy { subTaskEntities.map { it.subTask } }
    val hasSubTask: Boolean by lazy { subTaskEntities.isNotEmpty() }

    val superTask: String = superTaskEntity?.superTask ?: ""
    val subTasks: List<String> = subTaskEntities.map { it.subTask }
    val hasSubTask: Boolean = subTaskEntities.isNotEmpty()

    private var _superTask: String? = null
        private set
    val superTask: String
        get() {
            if (_superTask == null) _superTask = superTaskEntity?.superTask ?: ""
            return _superTask!!
        }
    private var _subTasks: List<String>? = null
        private set
    val subTasks: List<String>
        get() {
            if (_subTasks == null) _subTasks = subTaskEntities.map { it.subTask }
            return _subTasks!!
        }

    var isPossibleToDeclareVariables = false // Is impossible to declare variables with value in this class.
    */

    val superTask: String get() = superTaskEntity?.superTask ?: ""
    val subTasks: List<String> get() = subTaskEntities.map { it.subTask }
    val hasSubTasks: Boolean get() = subTaskEntities.isNotEmpty()
    val hasSuperTask: Boolean get() = superTaskEntity != null


    fun toModel(): TaskModel {
        return if (hasSubTasks) {
            task.toModel()
        } else {
            TODO("Task model has to be modified.")
        }
    }
}

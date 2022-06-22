package com.example.taskscheduler.data.sources.local

import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.util.TaskDataFlow
import com.example.taskscheduler.util.TaskTypeDataFlow
import kotlinx.coroutines.flow.Flow

interface ILocalTaskRepository {
    fun getTaskPagingSource(): TaskDataFlow

    fun getTaskPagingSourceBySuperTask(superTask: ITaskTitleOwner): TaskDataFlow

    fun getTaskPagingSourceByTaskType(type: ITaskTypeNameOwner): TaskDataFlow

    fun getTaskTypePagingSource(): TaskTypeDataFlow

    suspend fun existsTitle(taskTitle: String): Boolean


    suspend fun changeDone(task: ITaskTitleOwner, newValue: Boolean): Boolean

    suspend fun changeTaskDescription(task: ITaskTitleOwner, newValue: String): Boolean

    suspend fun changeTaskTitle(task: ITaskTitleOwner, newValue: String): Boolean

    suspend fun changeType(newValue: String, oldValue: String): Boolean

    suspend fun changeTypeInTaskHierarchy(task: String, newValue: String): Boolean


    suspend fun saveNewTask(newTask: TaskModel)

    fun getAllTasks(): Flow<List<TaskModel>>

    suspend fun getAllTasksStatic(): List<TaskModel>

    fun getTaskByTitle(title: String): Flow<TaskModel>

    suspend fun getTaskByTitleStatic(title: String): TaskModel

    suspend fun getTaskTypeByTitleStatic(title: String): String

    suspend fun getTaskTypeFromTask(task: TaskModel): TaskTypeModel

    companion object {
        const val PAGE_SIZE = 30
    }
}

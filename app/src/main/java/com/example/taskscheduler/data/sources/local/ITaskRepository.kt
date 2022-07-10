package com.example.taskscheduler.data.sources.local

import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.util.TaskDataFlow
import com.example.taskscheduler.util.TaskTypeDataFlow
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    //READ
    //Get paging source
    fun getTaskPagingSource(): TaskDataFlow

    fun getTaskPagingSourceBySuperTask(superTask: ITaskTitleOwner): TaskDataFlow

    fun getTaskPagingSourceByTaskType(type: ITaskTypeNameOwner): TaskDataFlow

    fun getTopSuperTasksPagingSource(): TaskDataFlow

    fun getAllChildrenPagingSource(superTask: ITaskTitleOwner): TaskDataFlow

    //Get
    fun getTaskTypePagingSource(): TaskTypeDataFlow

    fun getAllTasks(): Flow<List<TaskModel>>

    suspend fun getAllTasksStatic(): List<TaskModel>

    fun getTaskByTitle(title: String): Flow<TaskModel>

    suspend fun getTaskByTitleStatic(title: String): TaskModel

    suspend fun getTaskTypeByTitleStatic(title: String): String

    suspend fun getTaskTypeFromTask(task: TaskModel): TaskTypeModel

    suspend fun getTasksByTypeStatic(type: String): List<TaskModel>

    suspend fun getTaskTitlesByTypeStatic(type: String): List<String>

    suspend fun getTitlesOfHierarchyOfTaskByTypeStatic(type: String): List<String>


    //Exists
    suspend fun existsTitle(taskTitle: String): Boolean

    suspend fun existsType(taskType: String): Boolean

    //WRITE
    //Save
    suspend fun saveNewTask(newTask: TaskModel)

    //Update
    suspend fun changeDone(task: ITaskTitleOwner, newValue: Boolean): Boolean

    suspend fun changeTaskDescription(task: ITaskTitleOwner, newValue: String): Boolean

    suspend fun changeTaskTitle(task: ITaskTitleOwner, newValue: String): Boolean

    suspend fun changeType(newValue: String, oldValue: String): Boolean

    suspend fun changeTypeInTaskHierarchy(task: String, newValue: String): Boolean

    //Delete
    suspend fun deleteSingleTask(task: ITaskTitleOwner): Boolean

    suspend fun deleteTaskAndAllChildren(task: ITaskTitleOwner): Boolean

    companion object {
        const val PAGE_SIZE = 30
    }
}

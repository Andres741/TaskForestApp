package com.example.taskscheduler.data.sources.local

import androidx.paging.PagingData
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.flow.Flow


interface ILocalTaskRepository {
    fun getPagingSource(): Flow<PagingData<TaskModel>>

    fun getPagingSourceBySuperTask(superTask: TaskModel): Flow<PagingData<TaskModel>>


    suspend fun existsTitle(taskTitle: String): Boolean

    suspend fun changeDone(task: TaskModel, newValue: Boolean): Boolean

    suspend fun saveNewTask(newTask: TaskModel)

    fun getAllTasks(): Flow<List<TaskModel>>

    suspend fun getAllTasksStatic(): List<TaskModel>

    fun getTaskByTitle(title: String): Flow<TaskModel>

    suspend fun getTaskByTitleStatic(title: String): TaskModel

    suspend fun getTaskTypeByTitleStatic(title: String): String

    companion object {
        const val PAGE_SIZE = 30
    }
}


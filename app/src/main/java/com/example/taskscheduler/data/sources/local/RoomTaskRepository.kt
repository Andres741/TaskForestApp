package com.example.taskscheduler.data.sources.local

import androidx.paging.*
import androidx.room.Transaction
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository.Companion.PAGE_SIZE
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperTask
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
): ILocalTaskRepository {

    private val pagingConfig = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE)

    override fun getTaskPagingSource(): Flow<PagingData<TaskModel>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { taskDao.getTaskPagingSource() }
    ).flow.map { it.map (TaskWithSuperAndSubTasks::toModel) }

    override fun getTaskPagingSourceBySuperTask(superTask: TaskModel): Flow<PagingData<TaskModel>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { taskDao.getTaskPagingSourceBySuperTask(superTask.title) }
    ).flow.map { it.map (TaskWithSuperAndSubTasks::toModel) }

    override fun getTaskPagingSourceByTaskType(type: TaskTypeModel): Flow<PagingData<TaskModel>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { taskDao.getTaskPagingSourceByType(type.name) }
    ).flow.map { it.map (TaskWithSuperAndSubTasks::toModel) }

    override fun getTaskTypePagingSource(): Flow<PagingData<TaskTypeModel>> = Pager(
        config = pagingConfig,
        pagingSourceFactory = { taskDao.getTaskTypePagingSource() }
    ).flow.map { it.map (TaskTypeFromDB::toModel) }

    override suspend fun existsTitle(taskTitle: String): Boolean = taskDao.containsStatic(taskTitle)

    override suspend fun changeDone(task: TaskModel, newValue: Boolean) = taskDao
        .changeDone(task.title, newValue) > 0

    @Transaction
    override suspend fun saveNewTask(newTask: TaskModel) {
        newTask.toEntity().let { taskEntity ->
            taskDao.insert(taskEntity)
        }
        newTask.toSuperTaskEntity()?.let { superTaskEntity ->
            subTaskDao.insert(superTaskEntity)
        }
    }

    override fun getAllTasks(): Flow<List<TaskModel>> = taskDao
        .getAllTasksWithSuperAndSubTasks().map {
            it.map (TaskWithSuperAndSubTasks::toModel)
        }

    override suspend fun getAllTasksStatic(): List<TaskModel> = taskDao
        .getAllTasksWithSuperAndSubTasksStatic().map (TaskWithSuperAndSubTasks::toModel)

    override fun getTaskByTitle(title: String): Flow<TaskModel> = taskDao.
        getTaskWithSuperTask(title).map(TaskWithSuperTask::toModel)

    override suspend fun getTaskByTitleStatic(title: String) = taskDao.
        getTaskWithSuperTaskStatic(title).toModel()

    override suspend fun getTaskTypeByTitleStatic(title: String): String = taskDao.
        getTypeStatic(title)

    override suspend fun getTaskTypeFromTask(task: TaskModel) = taskDao.
        getTypeFromDBByTaskStatic(task.type).toModel()


//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i(
//            "RoomTaskRepository",
//            "${if (msj != null) "$msj: " else ""}${toString()}"
//        )
//    }
}

package com.example.taskscheduler.data.sources.local

import androidx.paging.*
import com.example.taskscheduler.data.sources.local.ITaskRepository.Companion.PAGE_SIZE
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskAndSubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskEntity.toModel
import com.example.taskscheduler.domain.models.ITaskTitleOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.util.TaskTypeDataFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

interface ILocalTaskRepository: ITaskRepository

@Singleton
class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
    private val taskAndSubTaskDao: TaskAndSubTaskDao,
): ILocalTaskRepository {

    private val pagingConfig = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE)

    private fun taskDataFlowConstructor(source: () -> PagingSource<Int, TaskWithSuperAndSubTasks>) = Pager(
        config = pagingConfig,
        pagingSourceFactory = source
    ).flow.flowOn(Dispatchers.Default).map { it.map (::TaskModel) }

    override fun getTaskPagingSource() = taskDataFlowConstructor {
        taskDao.getTaskPagingSource()
    }

    override fun getTaskPagingSourceBySuperTask(superTask: ITaskTitleOwner) = taskDataFlowConstructor {
        taskDao.getTaskPagingSourceBySuperTask(superTask.taskTitle)
    }

    override fun getTaskPagingSourceByTaskType(type: ITaskTypeNameOwner) = taskDataFlowConstructor {
        taskDao.getTaskPagingSourceByType(type.typeName)
    }

    override fun getTopSuperTasksPagingSource() = taskDataFlowConstructor {
        taskDao.getTopSuperTasks()
    }

    override fun getAllChildrenPagingSource(superTask: ITaskTitleOwner) = taskDataFlowConstructor {
        taskDao.getAllChildrenPagingSource(superTask.taskTitle)
    }

    override suspend fun getAllChildrenTitlesStatic(superTask: ITaskTitleOwner) =
        taskDao.getAllChildrenTitlesStatic(superTask.taskTitle)


    override fun getTaskTypePagingSource(): TaskTypeDataFlow = Pager(
        config = pagingConfig,
        pagingSourceFactory = { taskDao.getTaskTypePagingSource() }
    ).flow.map { it.map (TaskTypeFromDB::toModel) }

    override suspend fun existsTitle(taskTitle: String): Boolean = taskDao.containsStatic(taskTitle)

    override suspend fun existsType(taskType: String): Boolean = taskDao.existsTypeStatic(taskType)


    override suspend fun changeDone(task: ITaskTitleOwner, newValue: Boolean) =
        taskDao.changeDone(task.taskTitle, newValue) > 0

    override suspend fun saveNewTask(newTask: TaskModel) =
        taskAndSubTaskDao.insert(newTask.toTaskEntities())

    override fun getAllTasks(): Flow<List<TaskModel>> =
        taskDao.getAllTasksWithSuperAndSubTasks().map {
            it.map (TaskWithSuperAndSubTasks::toModel)
        }

    override suspend fun getAllTasksStatic(): List<TaskModel> =
        taskDao.getAllTasksWithSuperAndSubTasksStatic().map (TaskWithSuperAndSubTasks::toModel)

    override suspend fun getAllTasksTitlesStatic(): List<String> =
        taskDao.getAllTitlesStatic()

    override fun getTaskByTitle(title: String): Flow<TaskModel> =
        taskDao.getTaskWithSuperAndSubTasks(title).map(TaskWithSuperAndSubTasks::toModel)

    override suspend fun getTaskByTitleStatic(title: String) =
        taskDao.getTaskWithSuperAndSubTasksStatic(title).toModel()

    override suspend fun getTaskTypeByTitleStatic(title: String): String =
        taskDao.getTypeStatic(title)

    override suspend fun getTaskTypeFromTask(task: TaskModel) =
        taskDao.getTypeFromDBByTaskStatic(task.type).toModel()

    override suspend fun getTasksByTypeStatic(type: String) =
        taskDao.getTasksByTypeStatic(type).toModel()

    override suspend fun getTaskTitlesByTypeStatic(type: String) =
        taskDao.getTaskTitlesByTypeStatic(type)

    override suspend fun getTitlesOfHierarchyOfTaskByTypeStatic(type: String) =
        taskDao.getTitlesOfHierarchyOfTaskByTypeStatic(type)

    override suspend fun getSuperTaskTitleStatic(subTask: ITaskTitleOwner) =
        subTaskDao.getSuperTaskStatic(subTask.taskTitle)


    override suspend fun changeTaskDescription(task: ITaskTitleOwner, newValue: String) =
        taskDao.changeDescription(task.taskTitle, newValue) > 0

    override suspend fun changeTaskTitle(task: ITaskTitleOwner, newValue: String) =
        taskAndSubTaskDao.updateTitle(task.taskTitle, newValue) > 0

    override suspend fun changeType(newValue: String, oldValue: String) =
        taskDao.updateType(oldValue, newValue) > 0

    override suspend fun changeTypeInTaskHierarchy(task: String, newValue: String) =
        taskAndSubTaskDao.changeTaskType(task, newValue) > 0


    override suspend fun deleteSingleTask(task: ITaskTitleOwner) =
        taskAndSubTaskDao.deleteSingleTask(task.taskTitle) > 0

    override suspend fun deleteAll() = taskAndSubTaskDao.deleteAll() > 0


    override suspend fun deleteTaskAndAllChildren(task: ITaskTitleOwner) =
        taskAndSubTaskDao.deleteTaskAndAllChildren(task.taskTitle) > 0


//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i(
//            "RoomTaskRepository",
//            "${if (msj != null) "$msj: " else ""}${toString()}"
//        )
//    }
}

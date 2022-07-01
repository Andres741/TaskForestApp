package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao.Companion.GET_TOP_SUPER_TASK_OF_TASK
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import com.example.taskscheduler.util.dataStructures.wrapperUnzip

/**
 * This dao contains the UPDATE and INSERT queries that implies taskTable and subTaskTable at the
 * same time.
 */
@Dao
abstract class TaskAndSubTaskDao {

    companion object {
        const val UPDATE_TASK_TITLE =
            "UPDATE $TASK_TABLE SET $TITLE_ID = :newValue WHERE $TITLE_ID = :oldValue"
        const val UPDATE_ONE_TASK_TYPE =
            "UPDATE $TASK_TABLE SET $TYPEa = :newValue WHERE $TITLE_ID = :key"
        const val UPDATE_MULTIPLE_TASK_TYPE =
            "UPDATE $TASK_TABLE SET $TYPEa = :newValue WHERE $TITLE_ID IN (:keys)"

        const val UPDATE_SUB_TASK_TITLE =
            "UPDATE $SUBTASK_TABLE SET $SUB_TASK_ID = :newValue WHERE $SUB_TASK_ID = :oldValue"
        const val UPDATE_SUPER_TASK_TITLE =
            "UPDATE $SUBTASK_TABLE SET $SUPER_TASKa = :newValue WHERE $SUPER_TASKa = :oldValue"
        const val UPDATE_SUPER_OF_TASK =
            "UPDATE $SUBTASK_TABLE SET $SUPER_TASKa = :newValue WHERE $SUB_TASK_ID = :key"
        const val UPDATE_SUPER_OF_ALL_TASKS =
            "UPDATE $SUBTASK_TABLE SET $SUPER_TASKa = :newValue WHERE $SUB_TASK_ID IN (:keys)"


        const val DELETE_TASK =
            "DELETE FROM $TASK_TABLE WHERE $TITLE_ID = :key"
        const val DELETE_TASKS =
            "DELETE FROM $TASK_TABLE WHERE $TITLE_ID IN (:keys)"

        const val DELETE_ALL_TASKS =
            "DELETE FROM $TASK_TABLE"

        const val DELETE_ALL_SUPER_TASK =
            "DELETE FROM $SUBTASK_TABLE"
        const val DELETE_SUB =
            "DELETE FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask"
        const val DELETE_SUBS =
            "DELETE FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID IN (:subTasks)"
        const val DELETE_SUPER =
            "DELETE FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"
        const val DELETE_ALL_TASK_CHILDREN =
            ""
    }

    @Query(SubTaskDao.GET_SUB_TASKS_OF_SUPER_TASK)
    protected abstract suspend fun getSubTasksOfSuperTask(superTask: String): List<String>

    @Query(SubTaskDao.GET_SUPER_TASK)
    protected abstract suspend fun getSuperTask(subTask: String): String

    @Query(SubTaskDao.GET_ALL_SUB_TASKS)
    protected abstract suspend fun getAllSubTasks(superTask: String): List<String>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertTaskEntity(taskEntity: TaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertSubTaskEntity(superTaskEntity: SubTaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAllSubTaskEntities(superTaskEntity: Iterable<SubTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAllTaskEntities(vararg entities: TaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAllTaskEntities(entities: Iterable<TaskEntity>)


    @Transaction
    open suspend fun insert(taskEntity: TaskEntity, superTaskEntity: SubTaskEntity) {
        insertTaskEntity(taskEntity)
        insertSubTaskEntity(superTaskEntity)
    }

    @Transaction
    open suspend fun insert(taskEntity: TaskEntity) {
        insertTaskEntity(taskEntity)
        insertSubTaskEntity(SubTaskEntity(taskEntity.title))
    }

    @Transaction
    open suspend fun insertAllPairs(taskEntitiesList: List<Pair<TaskEntity, SubTaskEntity>>) {
//        val (taskEntities, subTaskEntities) = taskEntitiesList.unzip() // O(n) performance
        val (taskEntities, subTaskEntities) = taskEntitiesList.wrapperUnzip() //O(1) performance
        insertAllTaskEntities(taskEntities)  //But access is slower, not clear if I am improving performance.
        insertAllSubTaskEntities(subTaskEntities)
    }

    @Transaction
    open suspend fun insertAll(taskEntities: List<TaskEntity>) {
        val subTaskEntities = taskEntities.map { SubTaskEntity(it.title) }
        insertAllTaskEntities(taskEntities)
        insertAllSubTaskEntities(subTaskEntities)
    }

    suspend fun insert(taskEntities: Pair<TaskEntity, SubTaskEntity>) {
        val (taskEntity, superTaskEntity) = taskEntities
        insert(taskEntity, superTaskEntity)
    }

    @Query(UPDATE_TASK_TITLE)
    protected abstract suspend fun updateTaskTitle(oldValue: String, newValue: String): Int
    @Query(UPDATE_ONE_TASK_TYPE)
    protected abstract suspend fun updateOneTaskType(key: String, newValue: String): Int  //Reverse order in arguments does not work for some reason
    @Query(UPDATE_MULTIPLE_TASK_TYPE)
    protected abstract suspend fun updateMultipleTaskType(keys: List<String>, newValue: String): Int

    @Query(UPDATE_SUB_TASK_TITLE)
    protected abstract suspend fun updateSubTaskTitle(oldValue: String, newValue: String): Int
    @Query(UPDATE_SUPER_TASK_TITLE)
    protected abstract suspend fun updateSuperTaskTitle(oldValue: String, newValue: String): Int
    @Query(UPDATE_SUPER_OF_TASK)
    protected abstract suspend fun updateSuperTaskOfTask(key: String, newValue: String): Int
    @Query(UPDATE_SUPER_OF_ALL_TASKS)
    protected abstract suspend fun updateSuperTaskOfAllTasks(keys: List<String>, newValue: String): Int

    @Query(GET_TOP_SUPER_TASK_OF_TASK)
    protected abstract suspend fun getTopSuperTaskOfTask(subTask: String): String


    @Transaction
    open suspend fun updateTitle(newValue: String, oldValue: String): Int =
        updateTaskTitle(newValue, oldValue) + updateSubTaskTitle(newValue, oldValue) +
        updateSuperTaskTitle(newValue, oldValue)

    @Transaction
    open suspend fun changeTaskType(task: String, newValue: String): Int {
        val topSuperTask = getTopSuperTaskOfTask(task)
        return changeTaskTypeHelper(topSuperTask, newValue)
    }

    private suspend fun changeTaskTypeHelper(task: String, newValue: String): Int {
        val numChanged = updateOneTaskType(task, newValue)

        val childTitlesList = getSubTasksOfSuperTask(task)

        return numChanged + childTitlesList.fold(0) { sum, child ->
            sum + changeTaskTypeHelper(child, newValue)
        }
    }

    @Query(DELETE_TASK)
    protected abstract suspend fun deleteTaskFromTaskTable(key: String): Int

    @Query(DELETE_TASKS)
    protected abstract suspend fun deleteTasksFromTaskTable(keys: List<String>): Int

    @Query(DELETE_ALL_TASKS)
    protected abstract suspend fun deleteAllTasks(): Int


    //Delete
    @Delete
    protected abstract suspend fun delete(key: SubTaskEntity)

    @Query(DELETE_ALL_SUPER_TASK)
    protected abstract suspend fun deleteAll(): Int

    @Query(DELETE_SUB)
    protected abstract suspend fun deleteSubTask(subTask: String): Int

    @Query(DELETE_SUBS)
    protected abstract suspend fun deleteSubTasks(subTasks: List<String>): Int

    @Query(DELETE_SUPER)
    protected abstract suspend fun deleteSuperTask(superTask: String): Int

    @Transaction
    open suspend fun deleteTask(task: String): Int {
        val superTask = getSuperTask(task)
        val subTasks = getSubTasksOfSuperTask(task)

        updateSuperTaskOfAllTasks(subTasks, superTask)

        deleteSubTask(task)
        return deleteTaskFromTaskTable(task)
    }

    @Transaction
    open suspend fun deleteAllTaskChildren(task: String): Int {
        val allChildren = getAllSubTasks(task)
        deleteSubTasks(allChildren)
        return deleteTasksFromTaskTable(allChildren)
    }

    @Transaction
    open suspend fun deleteTaskAndAllChildren(task: String): Int {
        deleteSubTask(task)
        return deleteTaskFromTaskTable(task) + deleteAllTaskChildren(task)
    }
}

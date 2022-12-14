package com.example.taskscheduler.data.sources.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.COUNT_OF_TYPEa
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    companion object {
        const val GET =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID = :key"
        const val GET_BY_SUPER_TASK =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET_SUB_TASKS_OF_SUPER_TASK})"
//            """
//                SELECT $TASK_TABLE.* FROM $TASK_TABLE INNER JOIN $SUBTASK_TABLE
//                    ON $TASK_TABLE.$TITLE_ID = $SUBTASK_TABLE.$SUB_TASK_ID
//                    WHERE $SUBTASK_TABLE.$SUPER_TASKa = :superTask
//            """
        const val GET_BY_TASK_TYPE =
            "SELECT * FROM $TASK_TABLE WHERE $TYPEa = :typeName ORDER BY $TITLE_ID"
        const val GET_TOP_SUPER_TASK_BY_TYPE =
            "SELECT * FROM $TASK_TABLE WHERE $TYPEa = :typeName AND $TITLE_ID in (${SubTaskDao.GET_All_TOP_SUPER_TASK})"
        const val GET_TOP_SUPER_TASK_TITLE_BY_TYPE =
            "SELECT $TITLE_ID FROM $TASK_TABLE WHERE $TYPEa = :typeName AND $TITLE_ID in (${SubTaskDao.GET_All_TOP_SUPER_TASK})"
        const val GET_TITLES_BY_TASK_TYPE =
            "SELECT $TITLE_ID FROM $TASK_TABLE WHERE $TYPEa = :typeName"
        const val GET_TOP_SUPER_TASKS =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET_All_TOP_SUPER_TASK})"

        const val GET_ALL_CHILDREN =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET_ALL_SUB_TASKS})"

        const val GET_ALL_CHILDREN_TITLES =
            "SELECT $TITLE_ID FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET_ALL_SUB_TASKS})"
        const val GET_ALL_TITLES_HIERARCHY =
            "SELECT $TITLE_ID FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET_ALL_IN_TITLES_HIERARCHY})"

        const val GET_ALL =
            "SELECT * FROM $TASK_TABLE ORDER BY $TITLE_ID"

        const val GET_ALL_TITLES =
            "SELECT $TITLE_ID FROM $TASK_TABLE"

        const val GET_TYPE =
            "SELECT $TYPEa FROM $TASK_TABLE WHERE $TITLE_ID = :key"

        const val SIZE =
            "SELECT COUNT(1) FROM $TASK_TABLE"

        const val IS_NOT_EMPTY =
            "SELECT EXISTS(SELECT 1 FROM $TASK_TABLE LIMIT 1)"

        const val IS_EMPTY =
            "SELECT NOT EXISTS(SELECT 1 FROM $TASK_TABLE LIMIT 1)"

        const val CONTAINS =
            "SELECT EXISTS(SELECT 1 FROM $TASK_TABLE WHERE $TITLE_ID = :key)"

        const val EXISTS_TYPE =
            "SELECT EXISTS(SELECT 1 FROM $TASK_TABLE WHERE $TYPEa = :type LIMIT 1)"

        const val UPDATE_DONE =
            "UPDATE $TASK_TABLE SET $IS_DONEa = :newValue WHERE $TITLE_ID = :key"

        const val UPDATE_DESCRIPTION =
            "UPDATE $TASK_TABLE SET $DESCRIPTIONa = :newValue WHERE $TITLE_ID = :key"

        const val UPDATE_TYPE =
            "UPDATE $TASK_TABLE SET $TYPEa = :newValue WHERE $TYPEa = :oldValue"

        const val UPDATE_ADVISE_DATE =
            "UPDATE $TASK_TABLE SET $ADVISE_DATEa = :newValue WHERE $TITLE_ID = :key "


        const val GET_TYPE_FROM_DB_BY_TASK =
            "SELECT $TYPEa, COUNT($TYPEa) AS $COUNT_OF_TYPEa FROM $TASK_TABLE WHERE $TYPEa = :type GROUP BY $TYPEa"

        const val GET_TYPE_MULTIPLICITY =
            "SELECT COUNT($TYPEa) FROM $TASK_TABLE WHERE $TYPEa = :type GROUP BY $TYPEa"

        const val GET_All_TYPES_FROM_DB =
            "SELECT $TYPEa, COUNT($TYPEa) AS $COUNT_OF_TYPEa FROM $TASK_TABLE GROUP BY $TYPEa ORDER BY $TYPEa"
    }

    @Query(GET)
    suspend fun getStatic(key: String): TaskEntity
    @Query(GET)
    operator fun get(key: String): Flow<TaskEntity>
    @Transaction
    @Query(GET)
    suspend fun getTaskWithSuperAndSubTasksStatic(key: String): TaskWithSuperAndSubTasks
    @Transaction
    @Query(GET)
    fun getTaskWithSuperAndSubTasks(key: String): Flow<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(GET)
    suspend fun getTaskWithSuperTaskStatic(key: String): TaskWithSuperTask
    @Transaction
    @Query(GET)
    fun getTaskWithSuperTask(key: String): Flow<TaskWithSuperTask>

    @Transaction
    @Query(GET_BY_SUPER_TASK)
    fun getBySuperTask(superTask: String): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(GET_BY_SUPER_TASK)
    suspend fun getBySuperTaskStatic(superTask: String): List<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(GET_BY_SUPER_TASK)
    fun getTaskPagingSourceBySuperTask(superTask: String): PagingSource<Int, TaskWithSuperAndSubTasks>


    @Transaction
    @Query(GET_BY_TASK_TYPE)
    fun getTasksByType(typeName: String): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(GET_BY_TASK_TYPE)
    suspend fun getTasksByTypeStatic(typeName: String): List<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(GET_BY_TASK_TYPE)
    fun getTaskPagingSourceByType(typeName: String): PagingSource<Int, TaskWithSuperAndSubTasks>

    @Transaction
    @Query(GET_TOP_SUPER_TASK_BY_TYPE)
    suspend fun getTopSuperTaskByTypeStatic(typeName: String): TaskWithSuperAndSubTasks
    @Transaction
    @Query(GET_TOP_SUPER_TASK_TITLE_BY_TYPE)
    suspend fun getTopSuperTaskTileByTypeStatic(typeName: String): String?

    @Query(GET_TITLES_BY_TASK_TYPE)
    suspend fun getTaskTitlesByTypeStatic(typeName: String): List<String>

    @Transaction
    suspend fun getTitlesOfHierarchyOfTaskByTypeStatic(type: String): List<String> {
        val topTask = getTopSuperTaskTileByTypeStatic(type) ?: return emptyList()
        return getAllTitlesInHierarchyStatic(topTask)
    }

    @Transaction
    @Query(GET_TOP_SUPER_TASKS)
    fun getTopSuperTasks(): PagingSource<Int, TaskWithSuperAndSubTasks>


    @Transaction
    @Query(GET_ALL_CHILDREN)
    fun getAllChildren(superTask: String): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(GET_ALL_CHILDREN)
    fun getAllChildrenPagingSource(superTask: String): PagingSource<Int, TaskWithSuperAndSubTasks>

    @Transaction
    @Query(GET_ALL_CHILDREN_TITLES)
    suspend fun getAllChildrenTitlesStatic(superTask: String): List<String>
    @Transaction
    @Query(GET_ALL_TITLES_HIERARCHY)
    suspend fun getAllTitlesInHierarchyStatic(superTask: String): List<String>


    @Query(GET_TYPE)
    fun getType(key: String): Flow<String>
    @Query(GET_TYPE)
    suspend fun getTypeStatic(key: String): String


    @Query(GET_ALL)
    suspend fun getAllStatic(): List<TaskEntity>
    @Query(GET_ALL)
    fun getAll(): Flow<List<TaskEntity>>
    @Transaction
    @Query(GET_ALL)
    suspend fun getAllTasksWithSuperAndSubTasksStatic(): List<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(GET_ALL)
    fun getAllTasksWithSuperAndSubTasks(): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(GET_ALL)
    suspend fun getAllTasksWithSuperTaskStatic(): List<TaskWithSuperTask>
    @Transaction
    @Query(GET_ALL)
    fun getAllTasksWithSuperTask(): Flow<List<TaskWithSuperTask>>
    @Transaction
    @Query(GET_ALL)
    fun getTaskPagingSource(): PagingSource<Int, TaskWithSuperAndSubTasks>

    @Query(GET_ALL_TITLES)
    suspend fun getAllTitlesStatic(): List<String>


    @Query(SIZE)
    suspend fun sizeStatic(): Int
    @Query(SIZE)
    fun size(): Flow<Int>

    @Query(IS_EMPTY)
    suspend fun isEmptyStatic(): Boolean
    @Query(IS_EMPTY)
    fun isEmpty(): Flow<Boolean>

    @Query(IS_NOT_EMPTY)
    suspend fun isNotEmptyStatic(): Boolean
    @Query(IS_NOT_EMPTY)
    fun isNotEmpty(): Flow<Boolean>

    @Query(CONTAINS)
    fun contains(key: String): Flow<Boolean>
    @Query(CONTAINS)
    suspend fun containsStatic(key: String): Boolean

    @Query(EXISTS_TYPE)
    fun existsType(type: String): Flow<Boolean>
    @Query(EXISTS_TYPE)
    suspend fun existsTypeStatic(type: String): Boolean


    @Query(UPDATE_DONE)
    suspend fun changeDone(key: String, newValue: Boolean): Int

    @Query(UPDATE_DESCRIPTION)
    suspend fun changeDescription(key: String, newValue: String): Int

    @Query(UPDATE_TYPE)
    suspend fun updateType(oldValue: String, newValue: String): Int

    @Query(UPDATE_ADVISE_DATE)
    suspend fun updateAdviseDate(key: String, newValue: Long?): Int


    @Query(GET_TYPE_FROM_DB_BY_TASK)
    fun getTypeFromDBByTask(type: String): Flow<TaskTypeFromDB>
    @Query(GET_TYPE_FROM_DB_BY_TASK)
    suspend fun getTypeFromDBByTaskStatic(type: String): TaskTypeFromDB

    @Query(GET_TYPE_MULTIPLICITY)
    fun getTypeFromDB(type: String): Flow<Int>
    @Query(GET_TYPE_MULTIPLICITY)
    suspend fun getTypeFromDBStatic(type: String): Int

    @Query(GET_All_TYPES_FROM_DB)
    fun getAllTypesFromDB(): Flow<List<TaskTypeFromDB>>
    @Query(GET_All_TYPES_FROM_DB)
    suspend fun getAllTypesFromDBStatic(): List<TaskTypeFromDB>
    @Query(GET_All_TYPES_FROM_DB)
    fun getTaskTypePagingSource(): PagingSource<Int, TaskTypeFromDB>

}
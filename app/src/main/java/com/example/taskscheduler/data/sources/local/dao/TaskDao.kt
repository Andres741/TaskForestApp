package com.example.taskscheduler.data.sources.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.COUNT_OF_TYPEa
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface TaskDao {
    private companion object {
        const val GET =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID = :key"

        const val getBySuperTask =
            "SELECT * FROM $TASK_TABLE WHERE $TITLE_ID in (${SubTaskDao.GET})"
        //Shows the same item the same times as number of items that should show. A SQLite bug?
//            "SELECT $taskTable.* FROM $taskTable INNER JOIN $subtaskTable " +
//                    "ON $taskTable.$titleID = $subtaskTable.$superTask_a " +
//                    "WHERE $taskTable.$titleID = :superTask"

        const val GET_ALL =
            "SELECT * FROM $TASK_TABLE ORDER BY $TITLE_ID"

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

        const val CHANGE_DONE =
            "UPDATE $TASK_TABLE SET $IS_DONEa = :newValue WHERE $TITLE_ID = :key"

        const val DELETE =
            "DELETE FROM $TASK_TABLE WHERE $TITLE_ID = :key"

        const val DELETE_ALL =
            "DELETE FROM $TASK_TABLE"

        const val GET_TYPE_FROM_DB =
            "SELECT $TYPEa, COUNT($TYPEa) AS $COUNT_OF_TYPEa FROM $TASK_TABLE WHERE $TITLE_ID = :key GROUP BY $TYPEa"

        const val getAllTypesFromDB =
            "SELECT $TYPEa, COUNT($TYPEa) AS $COUNT_OF_TYPEa FROM $TASK_TABLE GROUP BY $TYPEa"
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
    @Query(getBySuperTask)
    fun getBySuperTask(superTask: String): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(getBySuperTask)
    fun getBySuperTaskStatic(superTask: String): List<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(getBySuperTask)
    fun getPagingSourceBySuperTask(superTask: String): PagingSource<Int, TaskWithSuperAndSubTasks>

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
    fun getPagingSource(): PagingSource<Int, TaskWithSuperAndSubTasks>

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


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: TaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Iterable<TaskEntity>)

    @Query(CHANGE_DONE)
    suspend fun changeDone(key: String, newValue: Boolean): Int

    @Query(DELETE)
    suspend fun delete(key: String)

    @Query(DELETE_ALL)
    suspend fun deleteAll()

    @Query(GET_TYPE_FROM_DB)
    fun getTypeFromDB(key: String): Flow<TaskTypeFromDB>
    @Query(GET_TYPE_FROM_DB)
    suspend fun getTypeFromDBStatic(key: String): TaskTypeFromDB

    @Query(getAllTypesFromDB)
    fun getAllTypesFromDB(): Flow<List<TaskTypeFromDB>>
    @Query(getAllTypesFromDB)
    suspend fun getAllTypesFromDBStatic(): List<TaskTypeFromDB>

    @Transaction
    suspend fun refresh(vararg data: TaskEntity) {
        deleteAll()
        insertAll(*data)
    }
    @Transaction
    suspend fun refresh(data: Iterable<TaskEntity>) {
        deleteAll()
        insertAll(data)
    }
}
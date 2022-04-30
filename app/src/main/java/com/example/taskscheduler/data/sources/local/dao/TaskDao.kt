package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.countOfType_a
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    private companion object {
        const val get = "SELECT * FROM $taskTable WHERE $titleID = :key"
        const val getAll = "SELECT * FROM $taskTable ORDER BY $titleID"
        const val size = "SELECT COUNT(1) FROM $taskTable"
        const val isNotEmpty = "SELECT EXISTS(SELECT 1 FROM $taskTable LIMIT 1)"
        const val isEmpty = "SELECT NOT EXISTS(SELECT 1 FROM $taskTable LIMIT 1)"
        const val contains = "SELECT EXISTS(SELECT 1 FROM $taskTable WHERE $titleID = :key)"
        const val delete = "DELETE FROM $taskTable WHERE $titleID = :key"
        const val deleteAll = "DELETE FROM $taskTable"
        const val getTypeFromDB = "SELECT $type_a, COUNT(type) AS $countOfType_a FROM $taskTable WHERE $titleID = :key GROUP BY $type_a"
        const val getAllTypesFromDB = "SELECT $type_a, COUNT(type) AS $countOfType_a FROM $taskTable GROUP BY $type_a"
    }

    @Query(get)
    suspend fun getStatic(key: String): TaskEntity
    @Query(get)
    operator fun get(key: String): Flow<TaskEntity>
    @Transaction
    @Query(get)
    suspend fun getTaskWithSuperAndSubTasksStatic(key: String): TaskWithSuperAndSubTasks
    @Transaction
    @Query(get)
    fun getTaskWithSuperAndSubTasks(key: String): Flow<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(get)
    suspend fun getTaskWithSuperTaskStatic(key: String): TaskWithSuperTask
    @Transaction
    @Query(get)
    fun getTaskWithSuperTask(key: String): Flow<TaskWithSuperTask>

    @Query(getAll)
    suspend fun getAllStatic(): List<TaskEntity>
    @Query(getAll)
    fun getAll(): Flow<List<TaskEntity>>
    @Transaction
    @Query(getAll)
    suspend fun getAllTasksWithSuperAndSubTasksStatic(): List<TaskWithSuperAndSubTasks>
    @Transaction
    @Query(getAll)
    fun getAllTasksWithSuperAndSubTasks(): Flow<List<TaskWithSuperAndSubTasks>>
    @Transaction
    @Query(getAll)
    suspend fun getAllTasksWithSuperTaskStatic(): List<TaskWithSuperTask>
    @Transaction
    @Query(getAll)
    fun getAllTasksWithSuperTask(): Flow<List<TaskWithSuperTask>>


    @Query(size)
    suspend fun sizeStatic(): Int
    @Query(size)
    fun size(): Flow<Int>

    @Query(isEmpty)
    suspend fun isEmptyStatic(): Boolean
    @Query(isEmpty)
    fun isEmpty(): Flow<Boolean>

    @Query(isNotEmpty)
    suspend fun isNotEmptyStatic(): Boolean
    @Query(isNotEmpty)
    fun isNotEmpty(): Flow<Boolean>

    @Query(contains)
    fun contains(key: String): Flow<Boolean>
    @Query(contains)
    fun containsStatic(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: TaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Iterable<TaskEntity>)

    @Query(delete)
    suspend fun delete(key: String)

    @Query(deleteAll)
    suspend fun deleteAll()

    @Query(getTypeFromDB)
    fun getTypeFromDB(key: String): Flow<TaskTypeFromDB>
    @Query(getTypeFromDB)
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
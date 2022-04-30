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
    @Query(get)
    suspend fun getTaskWithSubTasksStatic(key: String): TaskWithSubTasks
    @Query(get)
    fun getTaskWithSubTasks(key: String): Flow<TaskWithSubTasks>


    @Query(getAll)
    suspend fun getAllStatic(): List<TaskEntity>
    @Query(getAll)
    fun getAll(): Flow<List<TaskEntity>>
    @Query(getAll)
    suspend fun getAllTasksWithSubTasksStatic(): List<TaskWithSubTasks>
    @Query(getAll)
    fun getAllTasksWithSubTasks(): Flow<List<TaskWithSubTasks>>


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
    fun getTypeEntities(key: String): Flow<TaskTypeFromDB>
    @Query(getTypeFromDB)
    suspend fun getTypeEntityStatic(key: String): TaskTypeFromDB


    @Query(getAllTypesFromDB)
    fun getAllTypeEntities(): Flow<List<TaskTypeFromDB>>
    @Query(getAllTypesFromDB)
    suspend fun getAllTypeEntitiesStatic(): List<TaskTypeFromDB>

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
package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.TaskWithSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    private companion object {
        const val get = "SELECT * FROM $taskTable WHERE titleID = :key"
        const val getAll = "SELECT * FROM $taskTable ORDER BY titleID"
        const val size = "SELECT COUNT(1) FROM $taskTable"
        const val isNotEmpty = "SELECT EXISTS(SELECT 1 FROM $taskTable LIMIT 1)"
        const val isEmpty = "SELECT NOT EXISTS(SELECT 1 FROM $taskTable LIMIT 1)"
        const val contains = "SELECT EXISTS(SELECT 1 FROM $taskTable WHERE titleID = :key)"
        const val delete = "DELETE FROM $taskTable WHERE titleID = :key"
        const val deleteAll = "DELETE FROM $taskTable"
        const val getTypeEntity = "SELECT type, COUNT(type) AS multiplicity FROM $taskTable WHERE titleID = :key GROUP BY type"
        const val getAllTypeEntities = "SELECT type, COUNT(type) AS multiplicity FROM $taskTable GROUP BY type"
    }

    @Query(get)
    suspend fun getStatic(key: String): TaskEntity
    @Query(get)
    operator fun get(key: String): Flow<TaskEntity>
    @Transaction
    @Query(get)
    suspend fun getTaskWithSubTasksStatic(key: String): TaskWithSubTasks
    @Transaction
    @Query(get)
    fun getTaskWithSubTasks(key: String): Flow<TaskWithSubTasks>


    @Query(getAll)
    suspend fun getAllStatic(): List<TaskEntity>
    @Query(getAll)
    fun getAll(): Flow<List<TaskEntity>>
    @Transaction
    @Query(getAll)
    suspend fun getAllTasksWithSubTasksStatic(): List<TaskWithSubTasks>
    @Transaction
    @Query(getAll)
    fun getAllTasksWithSubTasks(): Flow<List<TaskWithSubTasks>>


    @Query(size)
    suspend fun sizeStatic(): Int
    @Query(size)
    fun size(): Flow<Int>


    //    suspend fun isEmpty() = size() == 0
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

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Iterable<TaskEntity>)

    //Delete
    @Query(delete)
    suspend fun delete(key: String)

    @Query(deleteAll)
    suspend fun deleteAll()

    @Query(getTypeEntity)
    fun getTypeEntities(key: String): Flow<TaskTypeFromDB>
    @Query(getTypeEntity)
    suspend fun getTypeEntityStatic(key: String): TaskTypeFromDB


    @Query(getAllTypeEntities)
    fun getAllTypeEntities(): Flow<List<TaskTypeFromDB>>
    @Query(getAllTypeEntities)
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
package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    private companion object {
        const val get = "SELECT subTaskID FROM subtask_table WHERE superTask = :superTask"
        const val getSubTaskEntities = "SELECT * FROM subtask_table WHERE superTask = :superTask"

        const val getAll = "SELECT * FROM subtask_table ORDER BY superTask"

        const val getAllSuperTasks = "SELECT superTask FROM subtask_table GROUP BY superTask"
        const val getSuperTask = "SELECT superTask FROM subtask_table WHERE subTaskID = :subTask"

        const val deleteAll = "DELETE FROM subtask_table"
        const val deleteSub = "DELETE FROM subtask_table WHERE subTaskID = :subTask"
        const val deleteSuper = "DELETE FROM subtask_table WHERE superTask = :superTask"
    }
    //Select
    @Query(getSubTaskEntities)
    fun getSubTaskEntities(superTask: String): Flow<List<SubTaskEntity>>
    @Query(getSubTaskEntities)
    suspend fun getSubTaskEntitiesStatic(superTask: String): List<SubTaskEntity>

    @Query(get)
    operator fun get(superTask: String): Flow<List<String>>
    @Query(get)
    suspend fun getStatic(superTask: String): List<String>

    @Query(getAll)
    fun getAll(): Flow<List<SubTaskEntity>>
    @Query(getAll)
    suspend fun getAllStatic(): List<SubTaskEntity>

    @Query(getAllSuperTasks)
    fun getAllSuperTasks(): Flow<List<String>>
    @Query(getAllSuperTasks)
    fun getAllSuperTasksStatic(): List<String>

    @Query(getSuperTask)
    fun getSuperTask(subTask: String): Flow<String?>
    @Query(getSuperTask)
    fun getSuperTaskStatic(subTask: String): String?

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: SubTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg list: SubTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: Iterable<SubTaskEntity>)

    //Delete
    @Delete
    suspend fun delete(key: SubTaskEntity)

    @Query(deleteAll)
    suspend fun deleteAll()

    @Query(deleteSub)
    suspend fun deleteSubTask(subTask: String)

    @Query(deleteSuper)
    suspend fun deleteSuperTask(superTask: String)

}

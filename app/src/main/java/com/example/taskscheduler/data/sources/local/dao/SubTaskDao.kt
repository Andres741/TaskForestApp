package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SUB_TASK_ID
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SUBTASK_TABLE
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SUPER_TASKa
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    companion object {
        const val GET =
            "SELECT $SUB_TASK_ID FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"
        const val GET_SUBTASK_ENTITIES =
            "SELECT * FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"

        const val GET_ALL =
            "SELECT * FROM $SUBTASK_TABLE ORDER BY $SUPER_TASKa"

        const val GET_ALL_SUPER_TASKS =
            "SELECT $SUPER_TASKa FROM $SUBTASK_TABLE GROUP BY $SUPER_TASKa"
        const val GET_SUPER_TASK = "SELECT $SUPER_TASKa FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask"

        const val DELETE_ALL =
            "DELETE FROM $SUBTASK_TABLE"
        const val DELETE_SUB =
            "DELETE FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask"
        const val DELETE_SUPER =
            "DELETE FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"
    }

    //Select
    @Query(GET_SUBTASK_ENTITIES)
    fun getSubTaskEntities(superTask: String): Flow<List<SubTaskEntity>>
    @Query(GET_SUBTASK_ENTITIES)
    suspend fun getSubTaskEntitiesStatic(superTask: String): List<SubTaskEntity>

    @Query(GET)
    operator fun get(superTask: String): Flow<List<String>>
    @Query(GET)
    suspend fun getStatic(superTask: String): List<String>

    @Query(GET_ALL)
    fun getAll(): Flow<List<SubTaskEntity>>
    @Query(GET_ALL)
    suspend fun getAllStatic(): List<SubTaskEntity>

    @Query(GET_ALL_SUPER_TASKS)
    fun getAllSuperTasks(): Flow<List<String>>
    @Query(GET_ALL_SUPER_TASKS)
    fun getAllSuperTasksStatic(): List<String>

    @Query(GET_SUPER_TASK)
    fun getSuperTask(subTask: String): Flow<String?>
    @Query(GET_SUPER_TASK)
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

    @Query(DELETE_ALL)
    suspend fun deleteAll()

    @Query(DELETE_SUB)
    suspend fun deleteSubTask(subTask: String)

    @Query(DELETE_SUPER)
    suspend fun deleteSuperTask(superTask: String)

}

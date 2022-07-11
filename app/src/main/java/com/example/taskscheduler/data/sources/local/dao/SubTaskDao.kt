package com.example.taskscheduler.data.sources.local.dao

import androidx.room.*
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    companion object {
        const val GET_SUB_TASKS_OF_SUPER_TASK =
            "SELECT $SUB_TASK_ID FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"
        const val GET_SUBTASK_ENTITIES =
            "SELECT * FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask"

        const val GET_ALL =
            "SELECT * FROM $SUBTASK_TABLE ORDER BY $SUPER_TASKa"

        const val GET_ALL_SUPER_TASKS =
            "SELECT $SUPER_TASKa FROM $SUBTASK_TABLE GROUP BY $SUPER_TASKa"
        const val GET_SUPER_TASK =
            "SELECT $SUPER_TASKa FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask"

        const val GET_All_TOP_SUPER_TASK =
            "SELECT $SUB_TASK_ID FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = '' "

        const val GET_ALL_FATHERS = """
            WITH RECURSIVE response AS ( 
                SELECT * FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask 
                UNION ALL
                SELECT tab.*
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUPER_TASKa = tab.$SUB_TASK_ID
            )
            SELECT * FROM response
        """  // On each recursive call res stores the last data selected.
        const val GET_ALL_FATHERS_BY_SUPER_TASK = """
            WITH RECURSIVE response AS (
                SELECT * FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask
                UNION ALL
                SELECT tab.*
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUPER_TASKa = tab.$SUB_TASK_ID
            )
            SELECT * FROM response
        """
        const val GET_ALL_SUPER_TASKS_OF_TASK = """
            WITH RECURSIVE response AS ( 
                SELECT $SUPER_TASKa FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask 
                UNION ALL
                SELECT tab.$SUPER_TASKa
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUPER_TASKa = tab.$SUB_TASK_ID
            )
            SELECT * FROM response
        """


        const val GET_ALL_IN_HIERARCHY = """ 
            WITH RECURSIVE response AS ( 
                SELECT * FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask 
                UNION ALL 
                SELECT tab.* 
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res 
                    ON res.$SUB_TASK_ID = tab.$SUPER_TASKa 
            ) 
            SELECT * FROM response 
        """
        const val GET_ALL_IN_TITLES_HIERARCHY = """ 
            WITH RECURSIVE response AS ( 
                SELECT $SUB_TASK_ID FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :superTask 
                UNION ALL 
                SELECT tab.$SUB_TASK_ID
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res 
                    ON res.$SUB_TASK_ID = tab.$SUPER_TASKa 
            ) 
            SELECT * FROM response 
        """
        const val GET_ALL_CHILDREN = """
            WITH RECURSIVE response AS (
                SELECT * FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask
                UNION ALL
                SELECT tab.*
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUB_TASK_ID = tab.$SUPER_TASKa
            )
            SELECT * FROM response
        """
        const val GET_ALL_SUB_TASKS = """
            WITH RECURSIVE response AS (
                SELECT $SUB_TASK_ID FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask
                UNION ALL
                SELECT tab.$SUB_TASK_ID
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUB_TASK_ID = tab.$SUPER_TASKa
            )
            SELECT * FROM response
        """


        const val GET_TOP_SUPER_TASK_OF_TASK = """
            WITH RECURSIVE response AS ( 
                SELECT * FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :subTask 
                UNION ALL
                SELECT tab.*
                    FROM $SUBTASK_TABLE AS tab INNER JOIN response AS res
                    ON res.$SUPER_TASKa = tab.$SUB_TASK_ID
            )
            SELECT $SUB_TASK_ID FROM response WHERE $SUPER_TASKa = ''
        """

        const val EXISTS_SUB_SASK =
            "SELECT EXISTS (SELECT 1 FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :key)"
        const val NOT_EXISTS_SUB_SASK =
            "SELECT NOT EXISTS (SELECT 1 FROM $SUBTASK_TABLE WHERE $SUB_TASK_ID = :key)"
        const val EXISTS_SUPER_SASK =
            "SELECT EXISTS (SELECT 1 FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask LIMIT 1)"
        const val NOT_EXISTS_SUPER_SASK =
            "SELECT NOT EXISTS (SELECT 1 FROM $SUBTASK_TABLE WHERE $SUPER_TASKa = :superTask LIMIT 1)"

        const val IS_TOP_SUPER_TASK =
            "SELECT '' = ($GET_SUPER_TASK)"

        const val IS_NOT_TOP_SUPER_TASK =
            "SELECT '' <> ($GET_SUPER_TASK)"
    }

    //Select
    @Query(GET_SUBTASK_ENTITIES)
    fun getSubTaskEntities(superTask: String): Flow<List<SubTaskEntity>>
    @Query(GET_SUBTASK_ENTITIES)
    suspend fun getSubTaskEntitiesStatic(superTask: String): List<SubTaskEntity>

    @Query(GET_SUB_TASKS_OF_SUPER_TASK)
    operator fun get(superTask: String): Flow<List<String>>
    @Query(GET_SUB_TASKS_OF_SUPER_TASK)
    suspend fun getStatic(superTask: String): List<String>

    @Query(GET_ALL)
    fun getAll(): Flow<List<SubTaskEntity>>
    @Query(GET_ALL)
    suspend fun getAllStatic(): List<SubTaskEntity>

    @Query(GET_ALL_SUPER_TASKS)
    fun getAllSuperTasks(): Flow<List<String>>
    @Query(GET_ALL_SUPER_TASKS)
    suspend fun getAllSuperTasksStatic(): List<String>

    @Query(GET_SUPER_TASK)
    fun getSuperTask(subTask: String): Flow<String>
    @Query(GET_SUPER_TASK)
    suspend fun getSuperTaskStatic(subTask: String): String

    @Query(GET_All_TOP_SUPER_TASK)
    fun getAllTopSuperTask(): Flow<List<String>>
    @Query(GET_All_TOP_SUPER_TASK)
    suspend fun getAllTopSuperTaskStatic(): List<String>

    @Query(EXISTS_SUB_SASK)
    suspend fun existsSubTask(key: String): Boolean
    @Query(NOT_EXISTS_SUB_SASK)
    suspend fun notExistsSubTask(key: String): Boolean
    @Query(EXISTS_SUPER_SASK)
    suspend fun existsSuperTask(superTask: String): Boolean
    @Query(NOT_EXISTS_SUPER_SASK)
    suspend fun notExistsSuperTask(superTask: String): Boolean

    @Query(IS_TOP_SUPER_TASK)
    suspend fun isTopSuperTask(subTask: String): Boolean
    @Query(IS_NOT_TOP_SUPER_TASK)
    suspend fun isNotTopSuperTask(subTask: String): Boolean

    @Query(GET_ALL_FATHERS_BY_SUPER_TASK)
    suspend fun getAllFathersBySuperTask(superTask: String): List<SubTaskEntity>
    @Query(GET_ALL_FATHERS)
    suspend fun getAllFathers(subTask: String): List<SubTaskEntity>
    @Query(GET_ALL_SUPER_TASKS_OF_TASK)
    suspend fun getAllSuperTasks(subTask: String): List<String>

    @Query(GET_ALL_IN_HIERARCHY)
    suspend fun getAllInHierarchy(subTask: String): List<SubTaskEntity>
    @Query(GET_ALL_IN_TITLES_HIERARCHY)
    suspend fun getAllTitlesInHierarchy(superTask: String): List<String>
    @Query(GET_ALL_CHILDREN)
    suspend fun getAllChildren(superTask: String): List<SubTaskEntity>
    @Query(GET_ALL_SUB_TASKS)
    suspend fun getAllSubTasks(superTask: String): List<String>

    @Query(GET_TOP_SUPER_TASK_OF_TASK)
    suspend fun getTopSuperTask(subTask: String): String

}

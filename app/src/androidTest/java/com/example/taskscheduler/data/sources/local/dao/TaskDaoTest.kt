package com.example.taskscheduler.data.sources.local.dao

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskscheduler.data.sources.local.LocalDataBase
import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.local.entities.TaskTypeFromDB
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.toModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var db: LocalDataBase
    private lateinit var taskDao: TaskDao
    private lateinit var subTaskDao: SubTaskDao

    private lateinit var listDBTasks: MutableList<TaskEntity>

    private val numTaskEntities = 10

    private fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LocalDataBase::class.java
        ).build()
        taskDao = db.taskDao
        subTaskDao = db.subTaskDao
    }

    private fun setUpTasks() = runBlocking {
        val list = mutableListOf<TaskEntity>()
        taskDao.insertAll(List(numTaskEntities, Int::taskNum).also { list.addAll(it) })
        listDBTasks = list
    }

    private fun setUpSubTasks() = runBlocking {
        val titles = taskDao.getAllStatic().map { it.title }

        subTaskDao.insertAll(listDBTasks.subList(1, numTaskEntities-1).map { SubTaskEntity(titles[0], it.title) })

        subTaskDao.insertAll(listDBTasks.subList(0, 4).map { SubTaskEntity(titles[3], it.title) })

        subTaskDao.insertAll(listDBTasks.subList(4, 7).map { SubTaskEntity(titles[9], it.title) })
    }

    @Before
    fun onBefore() {
        "\n/-----------------------------------------\\\n".log()
        createDb()
        setUpTasks()
        setUpSubTasks()
    }

    private fun closeDb() {
        db.close()
    }

    @After
    fun onAfter() {
        "\n\\-----------------------------------------/\n".log()
        closeDb()
    }

    @Test
    fun itWorks(): Unit = runBlocking {

        db.aDao.insertAll((listOf("Hello", "world", "of", "room").map { AEntity(data = it) }))

        db.aDao.getAll().forEach { it.log() }; "\n".log()

        "At least the database works.".log()
    }

    @Test
    fun simple_Write_And_Read_TaskEntity_Test(): Unit = runBlocking {
        taskDao.deleteAll()
        (0..3).forEach {
            taskDao.insert(it.taskNum())
        }
        taskDao.insertAll(List<TaskEntity>(7) {
            (it + 3).taskNum()
        })

        taskDao.getAllStatic().forEach(TaskEntity::log)
    }

    @Test
    fun taskDao_size_test(): Unit = runBlocking {
        assertEquals(numTaskEntities, taskDao.sizeStatic())
    }

    @Test
    fun taskDao_isEmpty_or_not_test(): Unit = runBlocking {
        assertFalse(taskDao.isEmptyStatic())
        assert(taskDao.isNotEmptyStatic())
        taskDao.deleteAll()
        assert(taskDao.isEmptyStatic())
        assertFalse(taskDao.isNotEmptyStatic())
    }

    @Test
    fun taskDao_get_test(): Unit = runBlocking {
        val aaa = taskDao.getStatic("t77").log()
        (aaa == null).log("Is null")  //Are you sure is always false?
        taskDao.getStatic("t3").log()
    }

    @Test
    fun taskDao_delete_test(): Unit = runBlocking {
        taskDao.delete("t77")
        taskDao.delete("t7")
        taskDao.delete("t3")
        taskDao.getAllStatic().forEach(TaskEntity::log)
    }

    @Test
    fun getAllSubTasks_test(): Unit = runBlocking  {
        subTaskDao.getAllStatic().forEach(SubTaskEntity::log)
    }

    @Test
    fun getSubTasks_test(): Unit = runBlocking  {
        "t0:".log()
        subTaskDao.getSubTaskEntities("t0").first().forEach(SubTaskEntity::log)
        "t3:".log()
        subTaskDao.getSubTaskEntitiesStatic("t3").forEach(SubTaskEntity::log)
        "t9:".log()
        subTaskDao.getSubTaskEntitiesStatic("t9").forEach(SubTaskEntity::log)
    }

    @Test
    fun deleteSub_test(): Unit = runBlocking  {
        subTaskDao.deleteSubTask("t1")
        subTaskDao.deleteSubTask("t5")
        subTaskDao.deleteSubTask("t55")

        subTaskDao.getAllStatic().forEach(SubTaskEntity::log)
    }

    @Test
    fun deleteSuper_test(): Unit = runBlocking  {
        subTaskDao.deleteSuperTask("t3")
        subTaskDao.getAllStatic().forEach(SubTaskEntity::log)
    }

    @Test
    fun getAllSuperTasks_test(): Unit = runBlocking  {
        subTaskDao.getAllSuperTasksStatic().forEach(String::log)
//        taskDao.get
    }

    @Test
    fun getAllTaskWithSuperAndSubTasks_test(): Unit = runBlocking  {
        taskDao.getAllTasksWithSuperAndSubTasks().first().forEach { taskWithSuperAndSubTasks ->
            val task = taskWithSuperAndSubTasks.toModel()
            task.log("task")
            task.superTask.log("super task")
            "List of sub tasks: ".log()
            task.subTasks.forEach(String::log)
            "\n".log()
        }
    }

    @Test
    fun getAllTaskWithSuperTask_test(): Unit = runBlocking  {
        taskDao.getAllTasksWithSuperTask().first().forEach { task ->
            task.log("task")
            task.superTaskEntity?.superTask.log("super task")
            "\n".log()
        }
    }

    @Test
    fun getBySuperTask_test(): Unit = runBlocking  {
        //t0 -> 2; t3 -> 4; t9 -> 3;
        taskDao.getBySuperTask("t3").first().toModel().forEach { task ->
            task.log("task")
            task.superTask.log("super task")
            "\n".log()
        }
    }

    @Test
    fun getAllTypeEntities_test(): Unit = runBlocking {
        repeat(7){ i ->
            taskDao.insert(TaskEntity("$i$i", "programming", "$i$i", date = 0L))
        }
        taskDao.getAllTypesFromDBStatic().forEach { it.log() }
    }

    @Test
    fun getTaskByType_test(): Unit = runBlocking {
        taskDao.getTaskByTypeStatic("t0").forEach { it.log("t0") }
        taskDao.getTaskByTypeStatic("t1").forEach { it.log("t1") }
        taskDao.getTaskByTypeStatic("t3").forEach { it.log("t3") }
        taskDao.getTaskByTypeStatic("t9").forEach { it.log("t9") }
    }
}

private fun Int.taskNum() = TaskEntity("t$this", "ty$this", "des$this", date = 0L)

//private fun Int.subTaskNum(sub: Int) = TaskEntity("t$this-$sub", "ty$this-$sub", "des$this-$sub", "super$this-$sub",)

private fun<T> T.log(msj: String? = null) = apply {
    Log.d("TaskDaoTest", "${if (msj != null) "$msj: " else ""}${toString()}")
}

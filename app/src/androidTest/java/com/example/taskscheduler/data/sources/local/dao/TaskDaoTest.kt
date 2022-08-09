package com.example.taskscheduler.data.sources.local.dao

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskscheduler.data.sources.local.LocalDataBase
import com.example.taskscheduler.data.sources.local.entities.taskEntity.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskEntity
import com.example.taskscheduler.data.sources.local.entities.taskEntity.toModel
import com.example.taskscheduler.domain.models.toTaskEntities
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import com.example.taskscheduler.util.taskModelTree_c
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private val taskModels by AsyncLazy { taskModelTree }

    private val taskEntities by AsyncLazy { taskModels.toTaskEntities() }

    private lateinit var db: LocalDataBase
    private val taskDao get() = db.taskDao
    private val subTaskDao get() = db.subTaskDao
    private val taskAndSubTaskDao get() = db.taskAndSubTaskDao

    private lateinit var listDBTasks: MutableList<TaskEntity>

    private val numTaskEntities = 10

    private fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LocalDataBase::class.java
        ).build()
    }

    private fun setUpTasks() = runBlocking {
        taskAndSubTaskDao.insertAllPairs(taskEntities)
    }

    private fun prepareDB() {
        createDb()
        setUpTasks()
    }

    private fun closeDb() {
        db.close()
    }

    private fun resetDB() {
        closeDb()
        prepareDB()
    }

    @Before
    fun onBefore() {
        "\n/-----------------------------------------\\\n".log()
        prepareDB()
    }

    @After
    fun onAfter() {
        "\n\\-----------------------------------------/\n".log()
        closeDb()
    }

    fun getAllModels() = runBlocking {
        taskDao.getAllTasksWithSuperAndSubTasksStatic().toModel()
    }

    fun showAllFromDB() = runBlocking {
        getAllModels().run {
            onEach {
                it.log()
                "".log()
            }
        }
    }

    @Test
    fun staticVSFlow(): Unit = runBlocking {
        val searched = "a"
        repeat(10) { i ->
            taskDao.changeDescription(searched, i.toString())
            assertEquals(taskDao[searched].first(), taskDao.getStatic(searched))
        }
    }

    @Test
    fun dataRecuperation_test(): Unit = runBlocking  {
        val recuperated = showAllFromDB()
        assertEquals(taskModels, recuperated)
    }


    @Test
    fun taskDao_size_test(): Unit = runBlocking {
        assertEquals(numTaskEntities, taskDao.sizeStatic())
    }

    @Test
    fun taskDao_isEmpty_or_not_test(): Unit = runBlocking {
        assertFalse(taskDao.isEmptyStatic())
        assert(taskDao.isNotEmptyStatic())
        taskAndSubTaskDao.deleteAll()
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
    fun getAllSubTasksEntities_test(): Unit = runBlocking  {
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
    fun getAllSuperTasks_test(): Unit = runBlocking  {
        subTaskDao.getAllSuperTasksStatic().forEach(String::log)
//        taskDao.get
    }

    @Test
    fun getAllTaskWithSuperTask_test(): Unit = runBlocking  {
        taskDao.getAllTasksWithSuperTask().first().forEach { task ->
            task.log("task")
            task.superTaskEntity.superTask.log("super task")
            "\n".log()
        }
    }

    @Test
    fun getBySuperTask_test(): Unit = runBlocking  {
        //t0 -> 2; t3 -> 4; t9 -> 3;
        val superTaskTitle = "b"
        val expectedDef = async {
            taskModels.filter { it.title != superTaskTitle && it.superTask.taskTitle == superTaskTitle}
        }
        val actual = taskDao.getBySuperTask(superTaskTitle).first().toModel().onEach { task ->
            task.log("task")
            "\n".log()
        }
        assertEquals(expectedDef.await(), actual)
    }

    @Test
    fun changeTaskType_test(): Unit = runBlocking {
        val time = measureTimeMillis {
            taskAndSubTaskDao.changeTaskType("a", "A").log("A count")
            taskAndSubTaskDao.changeTaskType("bbb", "B").log("B count")
            taskAndSubTaskDao.changeTaskType("ccccc", "C").log("C count")
            taskAndSubTaskDao.changeTaskType("x", "X").log("X count")
            taskAndSubTaskDao.changeTaskType("z", "Z").log("Z count")
        }
        showAllFromDB()
        time.log("Time")
    }

    @Test
    fun changeTaskTypeAsyncChannels_test(): Unit = runBlocking {
        val taskToNewTypeList = listOf(
            "a" to "A",
            "bbb" to "B",
            "ccccc" to "C",
            "x" to "X",
            "z" to "Z",
        )

        delay(100)
        val timeSync = measureTimeMillis {
            taskToNewTypeList.forEach { (task, newType) ->
                taskAndSubTaskDao.changeTaskType(task, newType).log("$newType count")
            }
        }
        val allDoneSync = showAllFromDB()

        resetDB()

        val timeAsync = withTimeout(1000) {
            delay(100)
            measureTimeMillis {
                taskToNewTypeList.forEach { (task, newType) ->
                    taskAndSubTaskDao.changeTaskTypeAsyncChannels(task, newType).log("$newType count")
                }
            }
        }

        val allDoneAsync = showAllFromDB()

        "timeSync: $timeSync,  timeAsync: $timeAsync".log()
        "${timeSync - timeAsync}".log("Absolute time improvement")
        "${timeSync.toDouble() / timeAsync}".log("Relative time improvement")

        assertEquals(allDoneSync, allDoneAsync)
    }

    @Test
    fun changeTaskTypeAsync_test(): Unit = runBlocking {
        val taskToNewTypeList = listOf(
            "a" to "A",
            "bbb" to "B",
            "ccccc" to "C",
            "x" to "X",
            "z" to "Z",
        )

        delay(200)
        val timeSync = measureTimeMillis {
            taskToNewTypeList.forEach { (task, newType) ->
                taskAndSubTaskDao.changeTaskType(task, newType).log("$newType count")
            }
        }
        val allDoneSync = showAllFromDB()

        resetDB()

        val timeAsync = withTimeout(1000) {
            delay(200)
            measureTimeMillis {
                taskToNewTypeList.forEach { (task, newType) ->
                    taskAndSubTaskDao.changeTaskTypeAsync(task, newType).log("$newType count")
                }
            }
        }

        val allDoneAsync = showAllFromDB()

        "timeSync: $timeSync,  timeAsync: $timeAsync".log()
        "${timeSync - timeAsync}".log("Absolute time improvement")
        "${timeSync.toDouble() / timeAsync}".log("Relative time improvement")

        assertEquals(allDoneSync, allDoneAsync)
    }

    @Test
    fun getAllFathers_test(): Unit = runBlocking {
        "fathers".bigLog()
        subTaskDao.getAllFathers("").log("Empty")
        subTaskDao.getAllFathers("c").log("c")
        subTaskDao.getAllFathers("cc").log("cc")
        subTaskDao.getAllFathers("ccccc").log("ccccc")
        "fathers and self".bigLog()
        subTaskDao.getAllFathersBySuperTask("").log("Empty")
        subTaskDao.getAllFathersBySuperTask("c").log("c")
        subTaskDao.getAllFathersBySuperTask("cc").log("cc")
        subTaskDao.getAllFathersBySuperTask("ccccc").log("ccccc")
        "all super tasks".bigLog()
        subTaskDao.getAllSuperTasks("").log("Empty")
        subTaskDao.getAllSuperTasks("c").log("c")
        subTaskDao.getAllSuperTasks("cc").log("cc")
        subTaskDao.getAllSuperTasks("ccccc").log("ccccc")
    }

    @Test
    fun getAllChildren_test(): Unit = runBlocking {
        "children".bigLog()
        subTaskDao.getAllChildren("").log("Empty")
        subTaskDao.getAllChildren("c").log("c")
        subTaskDao.getAllChildren("cc").log("cc")
        subTaskDao.getAllChildren("ccccc").log("ccccc")
        "children and self".bigLog()
        subTaskDao.getAllInHierarchy("").log("Empty")
        subTaskDao.getAllInHierarchy("c").log("c")
        subTaskDao.getAllInHierarchy("cc").log("cc")
        subTaskDao.getAllInHierarchy("ccccc").log("ccccc")
        "sub tasks".bigLog()
        subTaskDao.getAllSubTasks("").log("Empty")
        subTaskDao.getAllSubTasks("c").log("c")
        subTaskDao.getAllSubTasks("cc").log("cc")
        subTaskDao.getAllSubTasks("ccccc").log("ccccc")
    }

    @Test
    fun getTopSuperTask_test(): Unit = runBlocking {
        val tested = listOf("a", "aaa", "b", "bbb", "c", "cc", "ccccc", "z")

        tested.forEach { subTask ->
            subTaskDao.getTopSuperTask(subTask).log(subTask)
        }
    }

    @Test
    fun deleteSingleTask(): Unit = runBlocking {
        "Original".bigLog()
        showAllFromDB()
        taskAndSubTaskDao.deleteSingleTask("cc")
        "After deletion".bigLog()
        showAllFromDB()
        val superTask = "c"
        taskDao.getBySuperTask(superTask).first().toModel().forEach { task ->
            assertEquals(task.superTaskTitle, superTask)
        }
    }

    @Test
    fun deleteTaskAndChildren(): Unit = runBlocking {
        "Original".bigLog()
        showAllFromDB()
        taskAndSubTaskDao.deleteTaskAndAllChildren("cc")
        "After deletion".bigLog()
        showAllFromDB()
        val superTask = "c"
        val numSubTasks = taskDao.getAllChildren(superTask).first().toModel().size
        assertEquals(numSubTasks, 0)
    }
}

private fun Int.taskNum() = TaskEntity("t$this", "ty$this", "des$this", date = 0L, isDone = false, adviseDate = null)


//private fun Int.subTaskNum(sub: Int) = TaskEntity("t$this-$sub", "ty$this-$sub", "des$this-$sub", "super$this-$sub",)

private fun<T> T.log(msj: String? = null) = apply {
    Log.d("TaskDaoTest", "${if (msj != null) "$msj: " else ""}${toString()}")
}

private fun<T> T.bigLog(msj: String? = null) {
    "".log(); toString().uppercase().log(msj); "".log()
}

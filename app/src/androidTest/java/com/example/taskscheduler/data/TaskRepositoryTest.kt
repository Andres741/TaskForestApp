package com.example.taskscheduler.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.taskscheduler.data.sources.local.LocalDataBase
import com.example.taskscheduler.data.sources.local.RoomTaskRepository
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasks
import com.example.taskscheduler.data.sources.remote.netClases.toModel
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import com.example.taskscheduler.util.taskModelTree_c
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase
import kotlinx.coroutines.*

class TaskRepositoryTest: TestCase() {

    private val taskModels by AsyncLazy { taskModelTree }
    private val taskModels_c by AsyncLazy { taskModelTree_c }


    private val db: LocalDataBase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext<Context>(), LocalDataBase::class.java
    ).build()
    private val taskDao get() = db.taskDao
    private val subTaskDao get() = db.subTaskDao
    private val taskAndSubTaskDao get() = db.taskAndSubTaskDao

    private val localTaskRepository = RoomTaskRepository(taskDao, subTaskDao, taskAndSubTaskDao)

    private val collectionPath = "users/zzz_test/tasks/TaskRepositoryTest/tasks"
    private val firestoreTasks = FirestoreTasks(Firebase.firestore.collection(collectionPath))


    private val taskRepository = TaskRepository(localTaskRepository, firestoreTasks)

    init {
        runBlocking {
            taskRepository.deleteAll()
        }
    }

    public override fun setUp() = runBlocking {
        super.setUp()
        "\n/-----------------------------------------\\\n".log()
        taskModels_c.forEach { task ->
            taskRepository.saveNewTask(task)
        }
    }

    public override fun tearDown(): Unit = runBlocking {
        "\n\\-----------------------------------------/\n".log()
        taskRepository.deleteAll()
    }

    fun testRead(): Unit = runBlocking {
        taskModels.forEach { task ->
            taskRepository.saveNewTask(task)
        }
        firestoreTasks.getAllTasks().fold({
            val firestoreTask = it.toModel().logList("firestore")
            val repoTasks = localTaskRepository.getAllTasksStatic().logList("repo")
            assertEquals("Sizes are not the same", repoTasks.size, firestoreTask.size)
            repoTasks.forEachIndexed { index, repoTask ->
                val fireTask = firestoreTask[index]
                assertEquals(repoTask, fireTask)
            }
        }){ t ->
            t.log()
            assert(false) { t }
        }
    }

    fun testAllMethods(): Unit = runBlocking {
        val changes: List<Pair<String, suspend () -> Unit>> = withContext(Dispatchers.Default) {
            launch {
                taskModels.forEach { task ->
                    taskRepository.saveNewTask(task)
                }
            }
            listOf(
                "Original list" to {},
                "change Done" to
                        { taskRepository.changeDone("a".toTaskTitleOwner(), true) },
                "change Task Description" to
                        { taskRepository.changeTaskDescription("aa".toTaskTitleOwner(), "new description") },
                "change TaskTitle" to
                        { taskRepository.changeTaskTitle("aaa".toTaskTitleOwner(), "aAaAa") },
                "change Type_b" to
                        { taskRepository.changeType("bB", "b") },
                "change Type_c" to
                        { taskRepository.changeType("cC", "c") },
                "change Type In Task Hierarchy_ccc" to
                        { taskRepository.changeTypeInTaskHierarchy("ccc", "cCC") },
                "change Type In Task Hierarchy_x" to
                        { taskRepository.changeTypeInTaskHierarchy("x", "xX") },
                "delete Single Task_y" to
                        { taskRepository.deleteSingleTask("y".toTaskTitleOwner()) },
                "delete Single Task_cc" to
                        { taskRepository.deleteSingleTask("cc".toTaskTitleOwner()) },
                "delete Task And All Children_c" to
                        { taskRepository.deleteTaskAndAllChildren("c".toTaskTitleOwner()) },
                "delete Task And All Children_bb" to
                        { taskRepository.deleteTaskAndAllChildren("bb".toTaskTitleOwner()) },
                "delete All" to
                        { taskRepository.deleteAll() },
            )
        }


        changes.forEach { (name, action) ->
            name.bigLog()
            action()

            firestoreTasks.getAllTasks().fold({
                val repoTasks = localTaskRepository.getAllTasksStatic().logList("repo")
                val firestoreTask = it.toModel().logList("firestore")

                assertEquals("Sizes are not the same in $name", repoTasks.size, firestoreTask.size)
                repoTasks.forEachIndexed { index, repoTask ->
                    val fireTask = firestoreTask[index]
                    assertEquals("\nON $name\nREPO:\n$repoTask\nFIRE:\n$fireTask\n", repoTask, fireTask)
                }
            }){ t ->
                t.log()
                assert(false) { "in $name: $t" }
            }
        }
    }




    private fun String.toTaskTitleOwner() = SimpleTaskTitleOwner(this)

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.d("TaskRepositoryTest", "${if (msj != null) "$msj: " else ""}${toString()}")
    }

    private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
        "$msj:".uppercase().log()
        this.iterator().hasNext().takeIf { it } ?: kotlin.run {
            "  Collection is empty".log()
            return@apply
        }
        forEachIndexed { index, elem ->
            elem.log(index)
        }
    }

    private fun<T> T.bigLog(msj: Any? = null) = apply  {
        "".log(); toString().uppercase().log(msj); "".log()
    }
    private fun Throwable.log() = apply  {
        "Throwable: $this".log()
    }
}

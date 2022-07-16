package com.example.taskscheduler.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.taskscheduler.data.sources.local.LocalDataBase
import com.example.taskscheduler.data.sources.local.RoomTaskRepository
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasks
import com.example.taskscheduler.data.sources.remote.netClases.TaskDocument
import com.example.taskscheduler.data.sources.remote.netClases.toModel
import com.example.taskscheduler.domain.models.SimpleTaskTitleOwner
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import com.example.taskscheduler.util.taskModelTree_c
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase
import kotlinx.coroutines.*

class FirestoreSynchronizedTaskRepositoryTest: TestCase() {

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


    private val firestoreSynchronizedTaskRepository = FirestoreSynchronizedTaskRepository(localTaskRepository, firestoreTasks)

    init {
        runBlocking {
            firestoreTasks.deleteAllTasks()
        }
    }

    public override fun setUp() = runBlocking {
        super.setUp()
        "\n/-----------------------------------------\\\n".log()
        taskModels_c.forEach { task ->
            firestoreSynchronizedTaskRepository.saveNewTask(task)
        }
    }

    public override fun tearDown(): Unit = runBlocking {
        "\n\\-----------------------------------------/\n".log()
        firestoreSynchronizedTaskRepository.deleteAll()
    }

    fun testRead(): Unit = runBlocking {
        taskModels.forEach { task ->
            firestoreSynchronizedTaskRepository.saveNewTask(task)
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

    fun testCRUDMethods(): Unit = runBlocking {
        val changes: List<Pair<String, suspend () -> Unit>> = withContext(Dispatchers.Default) {
            launch {
                taskModels.forEach { task ->
                    firestoreSynchronizedTaskRepository.saveNewTask(task)
                }
            }
            listOf(
                "Original list" to {},
                "change Done" to
                        { firestoreSynchronizedTaskRepository.changeDone("a".toTaskTitleOwner(), true) },
                "change Task Description" to
                        { firestoreSynchronizedTaskRepository.changeTaskDescription("aa".toTaskTitleOwner(), "new description") },
                "change TaskTitle" to
                        { firestoreSynchronizedTaskRepository.changeTaskTitle("aaa".toTaskTitleOwner(), "aAaAa") },
                "change Type_b" to
                        { firestoreSynchronizedTaskRepository.changeType("bB", "b") },
                "change Type_c" to
                        { firestoreSynchronizedTaskRepository.changeType("cC", "c") },
                "change Type In Task Hierarchy_ccc" to
                        { firestoreSynchronizedTaskRepository.changeTypeInTaskHierarchy("ccc", "cCC") },
                "change Type In Task Hierarchy_x" to
                        { firestoreSynchronizedTaskRepository.changeTypeInTaskHierarchy("x", "xX") },
                "delete Single Task_y" to
                        { firestoreSynchronizedTaskRepository.deleteSingleTask("y".toTaskTitleOwner()) },
                "delete Single Task_cc" to
                        { firestoreSynchronizedTaskRepository.deleteSingleTask("cc".toTaskTitleOwner()) },
                "delete Task And All Children_c" to
                        { firestoreSynchronizedTaskRepository.deleteTaskAndAllChildren("c".toTaskTitleOwner()) },
                "delete Task And All Children_bb" to
                        { firestoreSynchronizedTaskRepository.deleteTaskAndAllChildren("bb".toTaskTitleOwner()) },
                "delete All" to
                        { firestoreSynchronizedTaskRepository.deleteAll() },
            )
        }


        changes.forEach { (testName, action) ->
            testName.bigLog()

            compare(testName) {
                action()
            }
        }
    }

    fun test_mergeLists(): Unit = runBlocking {
        compare("Original")

        firestoreTasks.deleteAllTasks()
        firestoreTasks.saveAll(
            TaskDocument(title = "d", type = "d", description = "",
                superTask = "", subTasks = listOf("dd", "ddd"), done = false, dateNum = 100
            ),
            TaskDocument(title = "dd", type = "d", description = "",
                superTask = "d", subTasks = emptyList(), done = false, dateNum = 200
            ),
            TaskDocument(title = "ddd", type = "d", description = "",
                superTask = "d", subTasks = emptyList(), done = false, dateNum = 300
            ),

            TaskDocument(title = "c", type = "cC", description = "You shouldn't see me",
                superTask = "...", subTasks = emptyList(), done = false, dateNum = 400
            ),
            TaskDocument(title = "cc", type = "cC", description = "You shouldn't see me neither",
                superTask = "c", subTasks = emptyList(), done = false, dateNum = 500
            ),
        )

        compare("merge lists") {
            firestoreSynchronizedTaskRepository.mergeLists()
        }
    }


    private fun compare(testName: String, test: (suspend () -> Unit)? = null): Unit = runBlocking {
        testName.bigLog()

        test?.apply {
            invoke()
            "--Output--".bigLog()
        }

        firestoreTasks.getAllTasks().fold({
            val repoTasks = localTaskRepository.getAllTasksStatic().logList("local")
            val firestoreTask = it.toModel().logList("firestore")

            assertEquals("Sizes are not the same in $testName", repoTasks.size, firestoreTask.size)
            repoTasks.forEachIndexed { index, repoTask ->
                val fireTask = firestoreTask[index]
                assertEquals("\nON $testName\nLOCAL:\n$repoTask\nFIRE:\n$fireTask\n", repoTask, fireTask)
            }
        }){ t ->
            t.log()
            assert(false) { "in $testName: $t" }
        }
    }


    private fun String.toTaskTitleOwner() = SimpleTaskTitleOwner(this)

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.d("FirestoreSynchronizedTaskRepositoryTest", "${if (msj != null) "$msj: " else ""}${toString()}")
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

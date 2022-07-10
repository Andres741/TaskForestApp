package com.example.taskscheduler.data.sources.remote.firestore

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskscheduler.data.sources.remote.netClases.IFirestoreDocument
import com.example.taskscheduler.data.sources.remote.netClases.TaskDocument
import com.example.taskscheduler.domain.models.toDocument
import com.example.taskscheduler.util.await
import com.example.taskscheduler.util.lazy.AsyncLazy
import com.example.taskscheduler.util.taskModelTree
import com.example.taskscheduler.util.taskModelTree_c
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import junit.framework.TestCase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.system.measureTimeMillis


@RunWith(AndroidJUnit4::class)
class FirestoreTasksTest {

    private val taskModels by AsyncLazy { taskModelTree }
    private val tasksJsons by AsyncLazy { taskModels.toDocument() }
    private val taskModels_c by AsyncLazy { taskModelTree_c }
    private val taskJsons_c by AsyncLazy { taskModels_c.toDocument() }
    private val taskTitles_c by AsyncLazy { taskJsons_c.map(TaskDocument::obtainDocumentName) }

//    private val collectionPath = "test/test_doc/tasks"
    private val collectionPath = "users/zzz_test/tasks"
    //private val collectionPath = "users/zz_test/tasks" //Grown
    private val firestoreTasks = FirestoreTasks(Firebase.firestore.collection(collectionPath))

    @Before
    fun onBefore(): Unit = runBlocking {
        firestoreTasks.saveAll(taskJsons_c)
        "\n/-----------------------------------------\\\n".log()
    }

    @After
    fun onAfter(): Unit = runBlocking {
        "\n\\-----------------------------------------/\n".log()
        firestoreTasks.deleteAllTasks()
    }

    @Test
    fun getTask_test(): Unit = runBlocking {
        val savedTask = tasksJsons[9].log("JSON saved")
        firestoreTasks.save(savedTask).onFailure { throwable ->
            assert(false) { "save task failed: $throwable" }
        }

        "non Existent Task Title".bigLog()

        val nonExistentTaskTitle = "abcdef"
        firestoreTasks.getTask(nonExistentTaskTitle).fold({ fireTask ->
            fireTask.log("From firestore")
            assert(false)
        }) { t ->
            "get non existent failed".log()
            t.log()
            assert(t !is ClassCastException) { "Task could not be casted" }
        }

        "existent Task map".bigLog()

        firestoreTasks.getTaskDocument(savedTask.title!!).fold({ document: DocumentSnapshot ->
            val fireTask: MutableMap<String, Any>? = document.data
            assertNotNull(fireTask); fireTask!!
            fireTask.log("From firestore")
            assert(true)

            assertNotNull(document.toObject<TaskDocument>())
        }) { t ->
            "get ${savedTask.title} failed".log()
            t.log()
            assert(false)
        }

        "existent Task Json".bigLog()

        firestoreTasks.getTask(savedTask.title!!).fold({ fireTask ->
            fireTask.log("From firestore")
            assert(true)
        }) { t ->
            "get ${savedTask.title} failed".log()
            t.log()
            assert(false)
        }
    }

    @Test
    fun getAllTasks_test(): Unit = runBlocking {
        withNotValidDataInFiresore {
            "all existent Task maps".bigLog()

            firestoreTasks.getTasksQuery().fold({ query ->
                query.documents.map(DocumentSnapshot::getData)
                    .forEachIndexed { index, docMap: MutableMap<String, Any>? ->
                        assertNotNull(docMap); docMap!!
                        docMap.log(index)
                    }
            }) { t ->
                t.log()
                assert(false) { t }
            }

            "all existent Task JSONs".bigLog()

            firestoreTasks.getAllTasks().fold({ tasks ->
                tasks.forEachIndexed { index, task ->
                    task.log(index)
                }
                assert(true)
            }) { t ->
                t.log()
                assert(false) { t }
            }
        }
    }

    @Test
    fun delete_test(): Unit = runBlocking {
        val task = tasksJsons[9].log("JSON saved")
        firestoreTasks.save(task).onFailure {
            assert(false) { "save task failed" }
        }

        firestoreTasks.delete(task.obtainDocumentName()).also { delay(2000) }.fold({
            "${task.title} deleted".log()
        }) { t ->
            "delete ${task.title} failed".log()
            t.log()
        }

        val nonExistentTaskTitle = "abcdef"
        firestoreTasks.delete(nonExistentTaskTitle).fold({
            "Non existent task deleted".log()
        }) { t ->
            "Non existent task was not deleted".log()
            t.log()
        }
    }

    @Test
    fun getTaskFlow_test(): Unit = runBlocking {
        val observedTask = "t99"
        val numTasks = 15

        val tasks = (0 until numTasks).map { index ->
            val strIndex = index.toString()
            TaskDocument(
                title = observedTask, type = strIndex, description = strIndex
            )
        }

        launch {
            "---Start saving task---".log()
            delay(50)
            tasks.forEach { task ->
                val time = measureTimeMillis {
                    firestoreTasks.save(task)
                }
                delay(0 - time)
            }
            "---Tasks saved---".log()
            firestoreTasks.delete(observedTask)
        }

        launch {
            var tasksCollected = 0
            try {
                "--- collect starts ---".log()
                firestoreTasks.getTaskDocumentsFlow(observedTask).collect { doc ->
                    val taskMap = doc.data
                    taskMap.log("task map")
                    taskMap ?: throw CancellationException()
                    tasksCollected++
                }
            } finally {
                "--- $tasksCollected collected ---".log()
                assertEquals(numTasks, tasksCollected)
            }
        }
    }

    @Test
    fun getTasksQueryFlow_test(): Unit = runBlocking {

        launch {
            val tasks = (0..2).map(Int::toTaskJson)
            "---Start saving task---".log()
            tasks.forEach { task ->
                firestoreTasks.save(task)
            }
            "---Tasks saved---".log()
            tasks.forEach { task ->
                firestoreTasks.delete(task.obtainDocumentName())
            }
            "---Tasks deleted---".log()
        }

        launch {
            try {
                "--- collect starts ---".log()
                withTimeoutOrNull(8000) {
                    firestoreTasks.getTasksQueryFlow().collect { query ->
                        val taskMaps: List<Map<String, Any>?> =
                            query.documents.map(DocumentSnapshot::getData)
                        "\n--- Collected list start\n".log()
                        taskMaps.forEach { taskMap ->
                            taskMap.log()
                        }
                        "\n--- Collected list ends\n".log()
                    }
                }
            } finally {
                "--- collection finished ---".log()
            }
        }
    }

    @Test
    fun get_Single_Tasks_List_From_Flow_test(): Unit = runBlocking {
        firestoreTasks.getAllTasksFlow().first().onEachIndexed { index, task ->
            task.log(index)
        }.also { tasks ->
            assert(taskJsons_c.containsAll(tasks))
            assertEquals(taskJsons_c.size, tasks.size)
        }
    }

    @Test
    fun get_Single_Tasks_Query_From_Flow_test(): Unit = runBlocking {
        firestoreTasks.getTasksQueryFlow().first().documents
            .map(DocumentSnapshot::getData).onEachIndexed { index, taskMaps ->
            taskMaps.log(index)
        }
    }

    @Test
    fun getTasksPageQueryFlow_test(): Unit = runBlocking {
        withAllInFirestore {
            val perPage = 5
            var previousTask: IFirestoreDocument? = null
            repeat(15) { page ->
                page.log("\n-- page:")
                firestoreTasks.getTasksPageFlow(previousTask, perPage).first().onEach { task ->
                    task.log()
                }.lastOrNull().also { lastTask ->
                    previousTask = lastTask
                }
            }
        }
    }





    // Testing help

    private suspend inline fun <T : Any> withThisInFirestore(
        vararg docsAndItems: Pair<String, T>, block: () -> Unit
    ) {
        val collection = firestoreTasks.tasksCollection
        collection.firestore.runBatch {
            docsAndItems.forEach {
                val (docName, item) = it
                collection.document(docName).set(mapOf(docName to item))
            }
        }.await()

        block()

        collection.firestore.runBatch {
            docsAndItems.forEach {
                val (docName, _) = it
                collection.document(docName).delete()
            }
        }.await()
    }
    private suspend inline fun withAllInFirestore(
        block: () -> Unit
    ) {
        //firestoreTasks.deleteAll(taskTitles_c)
        firestoreTasks.saveAll(tasksJsons)
        block()
        firestoreTasks.deleteAll(tasksJsons.map(TaskDocument::obtainDocumentName))
    }

    private suspend inline fun withNotValidDataInFiresore(block: () -> Unit) {
        withThisInFirestore(
            "doc0" to "doc0 cont",
            "doc1" to mapOf("doc1_1" to "1", "doc1_2" to "2", "doc1_2" to "2"),
            "doc2" to listOf("doc2[0]", "doc2[1]", "doc2[2]", "doc2[3]"),

            block = block
        )
    }

    @Test
    fun withExtraDataInFiresore_test(): Unit = runBlocking {
        val getTasks: suspend (Any) -> Unit = { msj ->
            msj.bigLog()

            firestoreTasks.getTasksQuery().onSuccess { query ->
                query.documents.map(DocumentSnapshot::getData)
                    .forEachIndexed { index, docMap: MutableMap<String, Any>? ->
                        docMap.log(index)
                    }
            }

        }
        getTasks("Extra data not added yet")

        withNotValidDataInFiresore {
            getTasks("Extra data added")
        }
        getTasks("Extra data deleted")
    }
}
private fun TaskDocument.toPair() = obtainDocumentName() to this

private fun Iterable<TaskDocument>.toPair() = map(TaskDocument::toPair)

private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("FirestoreTasksTest", "${if (msj != null) "$msj: " else ""}${toString()}")
}

private fun<T> T.bigLog(msj: Any? = null) = apply  {
    "".log(); toString().uppercase().log(msj); "".log()
}
private fun Throwable.log() = apply  {
    "Throwable: $this".log()
}

private fun Int.toTaskJson(): TaskDocument {
    val numStr = toString()
    return TaskDocument(
        title = numStr, type = numStr, description = numStr
    )
}
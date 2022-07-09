package com.example.taskscheduler.data.sources.remote.firestore

import com.example.taskscheduler.data.sources.remote.netClases.IFirestoreDocument
import com.example.taskscheduler.data.sources.remote.netClases.SimpleFirestoreDocument
import com.example.taskscheduler.data.sources.remote.netClases.TaskJson
import com.example.taskscheduler.data.sources.remote.netClases.setDoc
import com.example.taskscheduler.di.data.FirestoreCollectionForTasks
import com.example.taskscheduler.util.asFlow
import com.example.taskscheduler.util.await
import com.example.taskscheduler.util.dataStructures.asOtherTypeList
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FirestoreTasks(
    val tasksCollection: CollectionReference
) {
    val firestore = tasksCollection.firestore

    suspend fun save(task: TaskJson) = kotlin.runCatching {
        tasksCollection.setDoc(task).await()
    }
    suspend fun saveAll(vararg tasks: TaskJson) = saveAll(tasks.asList())


    suspend fun saveAll(tasks: List<TaskJson>) = kotlin.runCatching {
        firestore.runBatch {
            tasks.forEach { task ->
                tasksCollection.setDoc(task)
            }
        }.await()
    }

    suspend fun getTaskDocument(taskTitle: String) = kotlin.runCatching {
        tasksCollection.document(taskTitle).get().await()
    }
    suspend fun getTask(taskTitle: String) = getTaskDocument(taskTitle).mapCatching { doc ->
        doc.toObject<TaskJson>() ?: kotlin.run {
            if (doc?.data.isNullOrEmpty()) throw IllegalArgumentException("Task does not exists")
            throw ClassCastException("${doc.data} impossible to cast to TaskJson")
        }
    }

    fun getTaskDocumentsFlow(taskTitle: String) = tasksCollection.document(taskTitle).asFlow()
    fun getTasksFlow(taskTitle: String) = getTaskDocumentsFlow(taskTitle).map { doc ->
        doc.toObject<TaskJson>()
    }.filterNotNull()

    suspend fun getTasksQuery() = kotlin.runCatching {
        tasksCollection.get().await()
    }
    suspend fun getAllTasks() = getTasksQuery().mapCatching { query ->
        query.toObjects<TaskJson>()
    }
    suspend fun getAllTasksTitle() = getTasksQuery().mapCatching { query ->
        query.documents
            .asSequence()
            .map(DocumentSnapshot::getId).map(::SimpleFirestoreDocument)
            .toList()
    }


    fun getTasksQueryFlow() = tasksCollection.asFlow()
    fun getAllTasksFlow() = getTasksQueryFlow().map { query ->
        query.toObjects<TaskJson>()
    }
    fun getAllTasksTitleFlow() = getTasksQueryFlow().map { query ->
        query.documents
            .asSequence()
            .map(DocumentSnapshot::getId).map(::SimpleFirestoreDocument)
            .toList()
    }

    fun getTasksPageQueryFlow(
        previousTask: IFirestoreDocument? = null, perPage: Int
    ) = tasksCollection.orderBy("title").startAfter(previousTask.docName)
        .limit(perPage.toLong()).asFlow()

    fun getTasksPageFlow(
        previousTask: IFirestoreDocument? = null, perPage: Int
    ) = getTasksPageQueryFlow(previousTask, perPage).map { query ->
        query.toObjects<TaskJson>()
    }


    suspend fun delete(taskTitle: String) = kotlin.runCatching {
        tasksCollection.document(taskTitle).delete().await()
    }
    suspend fun deleteAll(vararg taskTitles: String) = deleteAll(taskTitles.asList())


    suspend fun deleteAll(taskTitles: Iterable<String>) {
        firestore.runBatch { batch ->
            taskTitles.forEach { title ->
                batch.delete(tasksCollection.document(title))
            }
        }.await()
    }
    suspend fun deleteAllTasks() {
        val titles = getAllTasksTitle().getOrNull() ?: return
        deleteAll(titles.asOtherTypeList(SimpleFirestoreDocument::obtainDocumentName))
    }

    private inline val IFirestoreDocument?.docName get() = this?.obtainDocumentName() ?: ""

//    private fun<T> T.log(msj: Any? = null) = apply {
//        Log.i("FirestoreTasks", "${if (msj != null) "$msj: " else ""}${toString()}")
//    }
}

/**
 * Contains a FirestoreTasks instance if the user is signed in, or null if does not.
 */
class FirestoreTasksAuth @Inject constructor(
    firestoreCollectionForTasks: FirestoreCollectionForTasks
) {
    val firestoreTasks: FirestoreTasks? = firestoreCollectionForTasks.collection?.let {
        FirestoreTasks(it)
    }
}

package com.example.taskscheduler.util

import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.util.dataStructures.MyLinkedList
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException

fun<T> Iterable<T>.toUnitHashMap(): HashMap<T, Unit> {
    if (this is Collection<T>) return toUnitHashMap()

    val list: MyLinkedList<T> = MyLinkedList(this)
    val map = HashMap<T, Unit>(list.size)

    list.normalIterator().forEach { item ->
        map[item] = Unit
    }
    return map
}

fun<T> Collection<T>.toUnitHashMap() = HashMap<T, Unit>(size).also { map ->
    forEach { item: T ->
        map[item] = Unit
    }
}

fun<T> Iterable<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Iterable<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> Collection<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Collection<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> MutableLiveData<T>.observeAgain() { value = value }


fun <T> Task<T>.getOrNull(): T? {
    return if (exception != null || isCanceled) null
    else result
}

suspend fun <T> Task<T>.await(): T {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                    "Task $this was cancelled normally.")
            } else {
                result
            }
        } else {
            throw e
        }
    }

    return withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
            addOnCanceledListener {
                cont.cancel()
            }.addOnSuccessListener {
                cont.resume(result,null)
            }.addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
        }
    }
}

fun DocumentReference.asFlow(): Flow<DocumentSnapshot> = callbackFlow {
    addSnapshotListener { document: DocumentSnapshot?, e ->
        if (e != null) {
            cancel(CancellationException("Firebase error", e))
            return@addSnapshotListener
        }
        if (document == null) {
            cancel(CancellationException("document is null"))
            return@addSnapshotListener
        }
        trySendBlocking(document).onFailure { t ->
            cancel(CancellationException("Error trying to send new value", t))
        }
    }.also { listener ->
        awaitClose { listener.remove() }
    }
}.flowOn(Dispatchers.IO)

fun Query.asFlow(): Flow<QuerySnapshot> = callbackFlow {
    addSnapshotListener { query: QuerySnapshot?, e ->
        if (e != null) {
            cancel(CancellationException("Firebase error", e))
            return@addSnapshotListener
        }
        if (query == null) {
            cancel(CancellationException("query is null"))
            return@addSnapshotListener
        }
        trySendBlocking(query).onFailure { t ->
            cancel(CancellationException("Error trying to send new value", t))
        }
    }.also { listener ->
        awaitClose { listener.remove() }
    }
}.flowOn(Dispatchers.IO)


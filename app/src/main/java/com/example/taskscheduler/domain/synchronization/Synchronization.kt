package com.example.taskscheduler.domain.synchronization

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

class WriteTaskContext @Inject constructor(): CoroutineContext by Dispatchers.Default + NonCancellable

@Singleton
class WriteTaskExclusion @Inject constructor(): Mutex by Mutex()

@Singleton
class WithWriteTaskContext @Inject constructor(
    val writeTaskContext: WriteTaskContext,
    val writeTaskExclusion: WriteTaskExclusion,
) {
    suspend inline operator fun <T> invoke(crossinline block: suspend () -> T): T {
        return withContext(writeTaskContext) {
            writeTaskExclusion.withLock {
                block()
            }
        }
    }
}

package com.example.taskscheduler.domain.synchronization

import com.example.taskscheduler.util.coroutines.SingleThreadNonCancellableContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SaveTaskContext @Inject constructor(): CoroutineContext by SingleThreadNonCancellableContext()

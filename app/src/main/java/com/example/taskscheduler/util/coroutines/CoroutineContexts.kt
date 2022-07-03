package com.example.taskscheduler.util.coroutines

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

//    private val singleThreadContext1 = newSingleThreadContext("saveTaskThread")   //Unrecommended
//    private val singleThreadContext2 = Dispatchers.Default.limitedParallelism(1)  //Experimental

private val singleThreadContext: CoroutineContext get() =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()  //The only without warnings

private val singleThreadNonCancellableContext: CoroutineContext get() =
    singleThreadContext + NonCancellable

class SingleThreadContext: CoroutineContext by singleThreadContext
class SingleThreadNonCancellableContext: CoroutineContext by singleThreadNonCancellableContext

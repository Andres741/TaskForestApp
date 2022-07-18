package com.example.taskscheduler.util.coroutines

import junit.framework.TestCase
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class SingleThreadContextTest : TestCase() {

    private val singleThreadContext = SingleThreadContext()
    private val singleThreadNonCancellableContext = SingleThreadNonCancellableContext()

    private val mutex: Mutex = Mutex()
    private val semaphore: Semaphore = Semaphore(1)


    override fun setUp() {
        "\n/-----------------------------------------\\\n".log()
    }

    override fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }



    fun test_sync(): Unit = runBlocking {
        "test sync".bigLog()

        repeat(5) { laun ->
            launch(singleThreadContext) {
                repeat(10) { rep ->
                    delay((0L..100L).random())
                    "Launch: $laun  rep: $rep".log()
                }
            }
        }
    }

    fun test_async(): Unit = runBlocking {
        "test async".bigLog()

        withContext(singleThreadContext){
            repeat(10) { laun ->
                launch(Dispatchers.IO) {
                    semaphore.withPermit {
                        repeat(20) { rep ->
                            delay((0L..100L).random())
                            "Launch: $laun  rep: $rep".log()
                        }
                    }
                }
            }
        }
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        println("${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logTime(msj: Any? = null) = apply {
        log("${System.currentTimeMillis()} ${ msj ?: "" }")
    }
    private fun<T> T.bigLog(msj: Any? = null) = apply {
        "".log(); toString().uppercase().log(msj); "".log()
    }
}
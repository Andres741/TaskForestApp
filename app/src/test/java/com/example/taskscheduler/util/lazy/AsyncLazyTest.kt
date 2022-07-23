package com.example.taskscheduler.util.lazy

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking

class AsyncLazyTest: TestCase() {

    private val listSize = 10000000

    private val charList by lazy {
        Array(listSize, Int::toChar).asList().also { "charList created".logTime() }
    }
    private val int16List by lazy { charList.map(Char::toShort).also { "int16List created".logTime() } }

    public override fun setUp() {
        "\n/-----------------------------------------\\\n".log()
    }

    public override fun tearDown() {
        "\n\\-----------------------------------------/\n".log()
    }

    fun testReadDelegated(): Unit = runBlocking {
        "readDelegated starts\n".logTime()

        val stringListSize = int16List.size.logTime("int16List.size")
        assertEquals(listSize, stringListSize)
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        //No sense error: Method d in android.util.Log not mocked.
//        Log.d("AsyncLazyTest", "${if (msj != null) "$msj: " else ""}${toString()}")

        println("${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logTime(msj: Any? = null) = apply {
        log("${System.currentTimeMillis()} ${ msj ?: "" }")
    }
    private fun<T> T.bigLog(msj: Any? = null) = apply {
        "".log(); toString().uppercase().log(msj); "".log()
    }

}

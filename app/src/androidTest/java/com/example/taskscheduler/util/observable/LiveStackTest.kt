package com.example.taskscheduler.util.observable

import android.util.Log
import com.example.taskscheduler.util.dataStructures.MyLinkedList
import junit.framework.TestCase
import kotlinx.coroutines.*

class LiveStackTest: TestCase() {

    lateinit var liveStack: LiveStack<String>
    lateinit var stack: MyLinkedList<String>

    private fun addToBoth(str: String): Unit = synchronized(this){
        stack.addFirst(str)
        liveStack.add(str)
    }

    private fun popBoth(): Pair<String?, String?> = synchronized(this){
        val second = stack.pop()
        val first = liveStack.pop()
        Pair(first, second)
    }

    private fun getBoth() = synchronized(this){ Pair(liveStack.value, stack.getFirst()) }

    private fun bothEquals() = synchronized(this){ liveStack.value == stack.getFirst() }

    public override fun setUp() {
        super.setUp()

        liveStack = LiveStack()
        stack = MyLinkedList()
        "\n/-----------------------------------------\\\n".log()

    }

    public override fun tearDown() {
        "\n\\-----------------------------------------/\n".log()

    }

    fun testLog() {
        "Hola?".log()
    }

    fun testCoroutines(): Unit = runBlocking {

        "testCoroutines: ".log()

        launch {
            "Hola".log(System.currentTimeMillis().toString())
            "desde".log(System.currentTimeMillis().toString()); delay(200)
            "corrutina".log(System.currentTimeMillis().toString())
        }
        delay(10)
        "--Hola sin corrutina--".log(System.currentTimeMillis().toString())
        assert(true)
    }

    fun testObserve(): Unit = runBlocking {

        "testObserve: ".log()
        "".log(); "".log()

        val timeMillis = 0L

        launch {
            withTimeout(5000) {
                withContext(Dispatchers.Main) {
                    liveStack.observeForever {

//                        stack.getFirst().log("Expected")
//                        it.log("Actual")
//                        "".log()
                        assertEquals (
                            stack.getFirst(),
                            it.log("New item in the liveStack")
                        )

                    }
                }
            }
        }

        launch {
            withContext(Dispatchers.Main) {

                addToBoth("0")
                delay(timeMillis); addToBoth("1")
                delay(timeMillis); addToBoth("2")
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
                delay(timeMillis); addToBoth("3")
                delay(timeMillis); addToBoth("4")
                delay(timeMillis); addToBoth("5")
                delay(timeMillis); addToBoth("6")
                delay(timeMillis); addToBoth("7")
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
                delay(timeMillis); popBoth()
            }
        }
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    Log.d("LiveStackTest", "${if (msj != null) "$msj: " else ""}${toString()}")
}

package com.example.taskscheduler.data

import junit.framework.TestCase

class ConvertersTest : TestCase() {

    val conv = Converters()
    val listOfString = listOf("hello", "world", "of", "JSON", "arrays")
    val jsonArrayOfString = "[\"hello\",\"world\",\"of\",\"JSON\",\"arrays\"]"

    public override fun setUp() {
        super.setUp()
        println("\n/-----------------------------------------\\\n")
    }

    public override fun tearDown() {
        println("\n\\-----------------------------------------/\n")
    }


    fun testCalendarToDatestamp() {

    }

    fun testDatestampToCalendar() {

    }

    fun testFromJsonArray() {
        val expected = listOfString

        Thread.sleep(200)
        val t0 = System.currentTimeMillis()
        val actual = conv.fromJsonArray(jsonArrayOfString)
        val t1 = System.currentTimeMillis()

        "testFromJsonArray".log()
        "Time required: ${t1-t0}ms".log()

        assertEquals(expected, actual.apply(::println))

    }

    fun testFromArrayList() {
        val expected = jsonArrayOfString

        Thread.sleep(200)
        val t0 = System.currentTimeMillis()
        val actual = conv.fromArrayList(listOfString)
        val t1 = System.currentTimeMillis()

        "testFromArrayList".log()
        "Time required: ${t1-t0}ms".log()

        assertEquals(expected, actual.apply(::println))
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    println("${if (msj != null) "$msj: " else ""}${toString()}")
}

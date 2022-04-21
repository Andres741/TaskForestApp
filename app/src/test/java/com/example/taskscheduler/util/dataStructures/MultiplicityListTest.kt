package com.example.taskscheduler.util.dataStructures

import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class MultiplicityListTest {

    /**The info must be put here and copied to mutableList and multiplicityList.*/
    lateinit var hashMap: HashMap<String, Int>
    /**This variable must have the same information of hashMap.*/
    lateinit var mutableList: MutableList<String>

    val numItems: Int get() = hashMap.size

    /**This variable must have the same information of hashMap and mutableList.*/
    lateinit var multiplicityList: MultiplicityList<String>

    /**
     * Sets the variables.
     */
    @Before
    fun onBefore() {
        println("\n/-----------------------------------------\\\n")

        multiplicityList = MultiplicityList()
        mutableList = mutableListOf()
        hashMap = hashMapOf("study" to 10, "work" to 15, "exercise" to 5, "others" to 6, "debugging" to 4)

        for (i in hashMap) {
            mutableList.add(i.key)
            (1..i.value).forEach{ _ -> multiplicityList.add(i.key) }
        }
    }

    @After
    fun onAfter() {
        println("\n\\-----------------------------------------/\n")
    }

    @Test
    fun testGetSize() {
        println("testGetSize()\n")

        assert(multiplicityList.size == numItems)
    }


    @Test
    fun testIsEmpty() {
        println("testIsEmpty()\n")

        val emptyMultiplicityList = MultiplicityList<String>()

        assert(! multiplicityList.isEmpty)
        assert(emptyMultiplicityList.isEmpty)
    }

    @Test
    fun testGet() {
        println("testGet()\n")

        for (i in 0 until numItems) {
            val isEquals = multiplicityList[i].also{ print("$it =? ")} == mutableList[i].apply(::print)
            println()
            assert(isEquals)
        }
    }

    @Test
    fun testMultiplicityOf() {
        println("testMultiplicityOf()\n")

        for ((k, v) in hashMap) {
            val isEquals = v == multiplicityList.multiplicityOf(k)
            assert(isEquals)
        }
    }
}

package com.example.taskscheduler.util

import java.util.*

data class SimpleDate (
    val year: Int,
    val month: Int,
    val day: Int,
)

fun Calendar.toSimpleDate() = SimpleDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
fun Long.toSimpleDate() = Calendar.getInstance().apply { time = Date(this@toSimpleDate) }.toSimpleDate()


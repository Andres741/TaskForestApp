package com.example.taskscheduler.util

import java.util.*

data class SimpleDate (
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<SimpleDate> {
    override fun compareTo(other: SimpleDate): Int {
        val isSuperior = year > other.year || year == other.year &&
                month > other.month || month == other.month &&
                day > other.day

        return if (isSuperior) 1 else if (day == other.day) 0 else -1
    }
}
fun Calendar.toSimpleDate() = SimpleDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
fun Long.toSimpleDate() = Calendar.getInstance().apply { time = Date(this@toSimpleDate) }.toSimpleDate()

data class SimpleTime(
    val hour: Int,
    val minute: Int,
) : Comparable<SimpleTime> {
    override fun compareTo(other: SimpleTime) = (hour * 60 + minute) - (other.hour * 60 + other.minute)
}
fun Calendar.toSimpleTime() = SimpleTime(this[Calendar.HOUR_OF_DAY], this[Calendar.MINUTE])

data class SimpleTimeDate(
    val date: SimpleDate,
    val time: SimpleTime,
) : Comparable<SimpleTimeDate> {
    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int): this(
        SimpleDate(year, month, day), SimpleTime(hour, minute)
    )

    override fun compareTo(other: SimpleTimeDate): Int {
        return date.compareTo(other.date).takeIf { it != 0 } ?: time.compareTo(other.time)
    }
    fun toCalendar(): Calendar = Calendar.getInstance().also { cal ->
        cal[Calendar.YEAR] = date.year
        cal[Calendar.MONTH] = date.month
        cal[Calendar.DAY_OF_MONTH] = date.day

        cal[Calendar.HOUR_OF_DAY] = time.hour
        cal[Calendar.MINUTE] = time.minute

        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
    }
}
fun Calendar.toSimpleTimeDate() = SimpleTimeDate(toSimpleDate(), toSimpleTime())

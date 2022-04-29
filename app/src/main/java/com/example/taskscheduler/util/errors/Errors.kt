package com.example.taskscheduler.util.errors

infix fun Int.outOfBoundsOf(index: Int) = IndexOutOfBoundsException("index: $index out of bound: $this")

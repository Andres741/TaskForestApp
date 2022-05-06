package com.example.taskscheduler.util

import timber.log.Timber

fun<T> T.log(msj: String? = null) = apply {
    Timber.d("${if (msj != null) "$msj: " else ""}${toString()}")
}

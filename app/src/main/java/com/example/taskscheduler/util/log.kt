package com.example.taskscheduler.util

import android.util.Log

fun <T> logFunConstructor(tag: Any): T.(Any?)-> T = { msj ->
    log(msj, tag)
}

fun <T> bigLogFunConstructor(tag: Any): T.(Any?)-> T = { msj ->
    bigLog(msj, tag)
}
fun <T> logNoMsjFunConstructor(tag: Any): T.()-> T = {
    log(tag = tag)
}

fun <T> bigLogNoMsjFunConstructor(tag: Any): T.()-> T = {
    bigLog(tag = tag)
}

fun logThrowableFunConstructor(tag: Any): Throwable.()-> Throwable = {
    log(tag)
}

private fun<T> T.log(msj: Any? = null, tag: Any) = apply {
    Log.d(tag.toString(), "${if (msj != null) "$msj: " else ""}${toString()}")
}

private fun<T> T.bigLog(msj: Any? = null, tag: Any) = apply  {
    "".log(tag = tag); toString().uppercase().log(msj, tag); "".log(tag = tag)
}
private fun Throwable.log(tag: Any) = apply  {
    "Throwable: $this".log(tag = tag)
}

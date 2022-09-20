package com.example.taskscheduler.util.lazy

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AsyncLazy<T: Any>(
    context: () -> CoroutineContext = { Dispatchers.Default },
    initializer: () -> T
): Lazy<T> {

    private var deferred: Deferred<T>? = CoroutineScope(context() + NonCancellable).async {
//        "pre delay".log()
//        delay(100)  //delay + await = blocked thread, not possible to suspend
//        "post delay".log()
        initializer().also {
            _value = it
            deferred = null
        }
    }

    private var _value: T? = null

    override val value: T
        get() = _value ?: runBlocking { deferred?.await() } ?: _value!!

    override fun isInitialized(): Boolean = _value != null

//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i("AsyncLazy", "${if (msj != null) "$msj: " else ""}${toString()}")
//    }
}

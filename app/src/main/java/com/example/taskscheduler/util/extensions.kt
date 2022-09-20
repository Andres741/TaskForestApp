package com.example.taskscheduler.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.R
import com.example.taskscheduler.util.dataStructures.MyLinkedList
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException

fun<T> Iterable<T>.toUnitHashMap() = HashMap<T, Unit>().also { map ->
    forEach { item: T ->
        map[item] = Unit
    }
}

fun<T> Iterable<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Iterable<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> Collection<T>.containsInConstantTime(): (T)-> Boolean = toUnitHashMap()::containsKey

fun<T> Collection<T>.notContainsInConstantTime(): (T)-> Boolean = toUnitHashMap()::notContainsKey

fun<T> MutableLiveData<T>.observeAgain() { value = value }

fun Regex.remove(charSequence: CharSequence) = replace(charSequence, "")

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
) = typedValue.let {
    theme.resolveAttribute(attrColor, it, resolveRefs)
    it.data
}

//fun Callendar.

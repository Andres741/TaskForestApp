package com.example.taskscheduler.util

import android.content.Context
import android.widget.Toast

fun notImplementedToastFactory(context: Context?, reason: String? = null): () -> Unit {
    return { Toast.makeText(
        context, "TODO${if (reason == null)"" else ".\nReason: $reason"}",
        Toast.LENGTH_SHORT
    ).show() }
}

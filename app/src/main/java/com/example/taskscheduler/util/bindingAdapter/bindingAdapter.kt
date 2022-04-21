package com.example.taskscheduler.util.bindingAdapter

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("baRenderHtml")
fun TextView.renderHtml(description: String?) {
//    text = description ?: ""  //To see pure HTML

    if (description != null) {
        text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        movementMethod = LinkMovementMethod.getInstance()
    } else {
        text = ""
    }
}

package com.example.taskscheduler.ui.main.adapters.bindingAdapters

import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter


@BindingAdapter("cardBackgroundImage")
fun CardView.setCardBackgroundImage(drawable: Int?) {
    drawable ?: return
    setBackgroundResource(drawable)
}

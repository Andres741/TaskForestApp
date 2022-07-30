package com.example.taskscheduler.ui.main.adapters.bindingAdapters

import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.TaskTypeItemBinding
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.util.getColorFromAttr

@BindingAdapter("baTextFromTaskType")
fun TextView.textTaskType(type: TaskTypeModel) {
    text = resources.getString(R.string.attribute_value_format, type.name, type.multiplicity.toString())
}

@BindingAdapter("baSetTaskTypeItemColor")
fun CardView.setTaskTypeItemColor(isSelected: Boolean) {
    val context = context ?: return

    setCardBackgroundColor(
        if (isSelected) context.getColorFromAttr(R.attr.selectedCardBackgroundColor)
        else context.getColorFromAttr(androidx.cardview.R.attr.cardBackgroundColor)
    )
}

@BindingAdapter("baSetTaskTypeItemTextColor")
fun TextView.setTaskTypeItemTextColor(isSelected: Boolean) {
    val context = context ?: return

    setTextColor(
        if (isSelected) context.getColorFromAttr(R.attr.overColorPrimary)
        else context.getColorFromAttr(R.attr.normalTextColor)
    )
}

fun TaskTypeItemBinding.setIsSelected(isSelected: Boolean) {
    cardRoot.setTaskTypeItemColor(isSelected)
    type.setTaskTypeItemTextColor(isSelected)
}

package com.example.taskscheduler.ui.adapters.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.TaskTypeModel

@BindingAdapter("baTextFromTaskType")
fun TextView.textTaskType(type: TaskTypeModel) {
    text = resources.getString(R.string.attribute_value_format, type.name, type.multiplicity.toString())
}

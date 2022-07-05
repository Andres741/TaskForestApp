package com.example.taskscheduler.ui.adapters.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.TaskModel

@BindingAdapter("baTextSubtasksText")
fun TextView.textSubtasksText(task: TaskModel) {
    val num = task.numSubTasks
    text = resources.getQuantityString(R.plurals.subtask_s , num, num)
}
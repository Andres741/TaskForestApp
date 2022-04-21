package com.example.taskscheduler.util.bindingAdapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.taskscheduler.R
import com.example.taskscheduler.data.models.TaskModel

@BindingAdapter("baTextSubtasksText")
fun TextView.textSubtasksText(task: TaskModel) {
    val num = task.numSubTasks
    text = resources.getQuantityString(R.plurals.subtask_s , num, num)
}
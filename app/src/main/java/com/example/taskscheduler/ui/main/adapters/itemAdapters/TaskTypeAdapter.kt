package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.TaskItemBinding
import com.example.taskscheduler.databinding.TaskTypeItemBinding
import com.example.taskscheduler.domain.models.TaskTypeModel

class TaskTypeAdapter: PagingDataAdapter<TaskTypeModel, TaskItemViewHolder>(TODO()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskItemViewHolder.create(parent)

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }
}

class TaskItemViewHolder private constructor(
    private val binding: TaskTypeItemBinding,
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup) = TaskItemViewHolder (
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.task_type_item,
                parent,
                false
            )
        )
    }

    fun bind(taskType: TaskTypeModel) {
        binding.taskType = taskType
    }
}

package com.example.taskscheduler.ui.adapters.fragmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.databinding.TaskItemBinding


class TasksAdapter: PagingDataAdapter<TaskModel, TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder.create(parent = parent)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }
}

class TaskViewHolder(
    private val binding: TaskItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TaskModel) {
        binding.apply {
            task = item
            executePendingBindings()
        }
    }

    companion object {
        fun create(parent: ViewGroup) = TaskViewHolder (
            binding = TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}

private class TaskDiffCallback : DiffUtil.ItemCallback<TaskModel>() {
    override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return oldItem == newItem
    }
}

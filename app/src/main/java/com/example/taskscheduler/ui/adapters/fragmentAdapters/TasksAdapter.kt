package com.example.taskscheduler.ui.adapters.fragmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.databinding.TaskItemBinding


class TasksAdapter(val viewModel: TaskAdapterViewModel): PagingDataAdapter<TaskModel, TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder.create(parent = parent, viewModel = viewModel)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }
}

class TaskViewHolder(
    private val binding: TaskItemBinding,
    val viewModel: TaskAdapterViewModel
): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, viewModel: TaskAdapterViewModel) = TaskViewHolder (
            binding = TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            viewModel = viewModel
        )
    }

    fun bind(item: TaskModel) {
        binding.apply {
            task = item
            doneCallBack = DoneCallBack()
            executePendingBindings()
        }
    }

    inner class DoneCallBack {
        operator fun invoke(){
            //viewModel.changeDoneStatusOf(binding.task)
            binding.task?.let(viewModel::changeDoneStatusOf) // Will this change the image of the checkButton?
        }
    }
    inner class GoToSubTaskDetailCallBack {
        operator fun invoke(){
            //viewModel.addToStack(binding.task)
            binding.task?.let(viewModel::addToStack)  // taskStack must be observed in the fragments.
        }
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

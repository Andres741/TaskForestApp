package com.example.taskscheduler.ui.adapters.itemAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.databinding.TaskItemBinding
import timber.log.Timber


class TasksAdapter(val viewModel: TasksAdapterViewModel): PagingDataAdapter<TaskModel, TaskViewHolder>(TaskDiffCallback()) {

    init { "TasksAdapter created".log() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder.create(parent = parent, viewModel = viewModel)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        getItem(position).log("Item")?.let { item ->
            holder.bind(item)
        }
    }
}

class TaskViewHolder private constructor(
    private val binding: TaskItemBinding,
    val viewModel: TasksAdapterViewModel
): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, viewModel: TasksAdapterViewModel) = TaskViewHolder (
//            binding = TaskItemBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            ),
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.task_type_item,
                parent,
                false
            ),
            viewModel = viewModel
        )
//        lateinit var viewModelComp: TasksAdapterViewModel
    }

    fun bind(item: TaskModel) {
        binding.apply {
            task = item
            doneCallBack = DoneCallBack()
            goToSubTaskDetailCallBack = GoToSubTaskDetailCallBack()
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

private fun<T> T.log(msj: String? = null) = apply {
    Log.i( "TasksAdapter","${if (msj != null) "$msj: " else ""}${toString()}")
}

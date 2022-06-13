package com.example.taskscheduler.ui.main.adapters.itemAdapters

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
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import kotlinx.coroutines.*


class TasksAdapter(
    val viewModel: TasksAdapterViewModel
): PagingDataAdapter<TaskModel, TaskViewHolder>(TaskDiffCallback) {

//    init { "TasksAdapter created".log() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(
            parent = parent, viewModel = viewModel
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }

    override fun onViewRecycled(holder: TaskViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }
}

class TaskViewHolder private constructor(
    private val binding: TaskItemBinding,
    val viewModel: TasksAdapterViewModel,
): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, viewModel: TasksAdapterViewModel) = TaskViewHolder (
            //            binding = TaskItemBinding.inflate(
            //                LayoutInflater.from(parent.context),
            //                parent,
            //                false
            //            ),
            binding = DataBindingUtil.inflate<TaskItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.task_item,
                parent,
                false
            ),
            viewModel = viewModel
        ).apply { setCallBacks() } //(::setCallBacks) // Doesn't work the reference.
    }

    private val scopeProvider = OneScopeAtOnceProvider()

    private inline val taskViewHolderScope get() = scopeProvider.currentScope

    private fun setCallBacks() {
        binding.apply {
            doneCallBack = DoneCallBack()
            goToSubTaskDetailCallBack = GoToSubTaskDetailCallBack()
        }
    }

    private fun setItem(item: TaskModel) {
        binding.apply {
            task = item
            executePendingBindings()
        }
    }

    fun bind(item: TaskModel) {
        setItem(item)
        scopeProvider.newScope
    }

    fun recycle() {
        scopeProvider.cancel()
    }

    inner class DoneCallBack {
        operator fun invoke() {
            taskViewHolderScope?.launch {
                // Completely safe, because taskViewHolderScope isn't null only when the viewHolder is bind.
                val task = binding.task!!
                if (viewModel.changeDoneStatusOf(task)) {
                    setItem(task)
                }
            }
        }
    }

    inner class GoToSubTaskDetailCallBack {
        operator fun invoke() {
            binding.task?.also(viewModel::addToStack)  // taskStack must be observed in the fragments.
        }
    }
}

private object TaskDiffCallback : DiffUtil.ItemCallback<TaskModel>() {
    override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return (oldItem.title == newItem.title)//.log("areItemsTheSame")
    }

    override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return (oldItem == newItem)//.log("areContentsTheSame")
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    Log.i( "TasksAdapter","${if (msj != null) "$msj: " else ""}${toString()}")
}

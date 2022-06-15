package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.databinding.TaskItemBinding
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TaskViewHolder.Companion.setTasksAdapterViewModel
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import kotlinx.coroutines.*

class TasksAdapter(
    viewModel: TasksAdapterViewModel
): PagingDataAdapter<TaskModel, TaskViewHolder>(TaskDiffCallback) {

    init {
        setTasksAdapterViewModel(viewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder.create(parent)

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
): RecyclerView.ViewHolder(binding.root) {

    companion object {
        private lateinit var viewModel: TasksAdapterViewModel

        fun setTasksAdapterViewModel(tasksAdapterViewModel: TasksAdapterViewModel) {
            viewModel = tasksAdapterViewModel
        }

        fun create(parent: ViewGroup) = TaskViewHolder (
            TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
//            DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.task_item,
//                parent,
//                false
//            )
        ).apply { setCallBacks() } //(::setCallBacks) // Doesn't work the reference.
    }

    private val scopeProvider = OneScopeAtOnceProvider()

    private inline val taskViewHolderScope get() = scopeProvider.currentScope
    //Safe in coroutines crated with taskViewHolderScope.
    private inline val taskInBinding: TaskModel get() = binding.task!!


    private fun setCallBacks() {
        binding.apply {
            doneCallBack = DoneCallBack()
            goToSubTaskDetailCallBack = GoToSubTaskDetailCallBack()

            taskType.setOnClickListener onClick@ {
                taskViewHolderScope?.launch {
                    val taskType = viewModel.getTaskTypeFromTask(taskInBinding)
                    viewModel.selectedTaskTypeName.value = taskType
                }
            }
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
                if (viewModel.changeDoneStatusOf(taskInBinding)) {
                    setItem(taskInBinding)
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

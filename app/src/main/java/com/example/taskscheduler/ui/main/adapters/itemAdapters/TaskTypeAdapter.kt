package com.example.taskscheduler.ui.main.adapters.itemAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.TaskTypeItemBinding
import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.util.OnClickTaskTypeVH

class TaskTypeAdapter (
    private val onClickCallBack: OnClickTaskTypeVH
): PagingDataAdapter<TaskTypeModel, TaskTypeViewHolder>(TaskTypeDiffCallback) {

    private val bindViewHolderMap = hashMapOf<TaskTypeModel, TaskTypeViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskTypeViewHolder.create(
        parent, onClickCallBack
    )

    override fun onBindViewHolder(holder: TaskTypeViewHolder, position: Int) {
        getItem(position)?.also { taskType ->
            holder.bind(taskType)
            bindViewHolderMap[taskType] = holder
        }
    }

    override fun onViewRecycled(holder: TaskTypeViewHolder) {
        bindViewHolderMap.remove(holder.taskTypeInBinding!!)
        super.onViewRecycled(holder)
    }

    fun selectViewHolder(taskType: TaskTypeModel) {
        TaskTypeViewHolder.selectedTaskType = taskType.log("taskType")
        bindViewHolderMap[taskType]?.setIsSelected()
    }
    fun unselectViewHolder() {
        TaskTypeViewHolder.removeSelected()
        //taskTypeToViewHolderMap[taskType]?.setColor()
    }
}

class TaskTypeViewHolder private constructor(
    private val binding: TaskTypeItemBinding,
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        var selectedViewHolder: TaskTypeViewHolder? = null
        var selectedTaskType: TaskTypeModel? = null

        const val greenColor = 0xff00ff55.toInt()
        const val whiteColor = 0xffffffff.toInt()

        fun removeSelected() {
            selectedTaskType = null
            val previousColored = selectedViewHolder?.binding?.root
            previousColored?.setBackgroundColor(whiteColor)
            selectedViewHolder = null
        }

        fun create(parent: ViewGroup, onClickCallBack: OnClickTaskTypeVH) = TaskTypeViewHolder(
            DataBindingUtil.inflate (
                LayoutInflater.from(parent.context),
                R.layout.task_type_item,
                parent,
                false
            )
        ).setCallBacks(onClickCallBack)
    }

    val taskTypeInBinding: TaskTypeModel? get() = binding.taskType
    val isSelected: Boolean
        get() {
            val selectedTaskType = selectedTaskType
            return selectedTaskType != null && binding.taskType == selectedTaskType
        }

    private fun setCallBacks(onClickCallBack: OnClickTaskTypeVH) = apply {
        binding.root.setOnClickListener onClick@ {
            val taskType = taskTypeInBinding ?: return@onClick
            if (selectedViewHolder?.taskTypeInBinding == taskType) {
                onClickCallBack(null)
                return@onClick
            }
            onClickCallBack(taskType)
        }
    }

    fun unselect() {
        binding.root.setBackgroundColor(whiteColor)
    }

    fun setIsSelected() {
        if (isSelected) {
            selectedViewHolder?.unselect()
            binding.root.setBackgroundColor(greenColor)
            selectedViewHolder = this
            return
        }
        binding.root.setBackgroundColor(whiteColor)
    }


    private fun setItem(taskType: TaskTypeModel) {
        binding.apply {
            this.taskType = taskType
            executePendingBindings()
        }
    }
    fun bind(taskType: TaskTypeModel) {
        setItem(taskType)
        setIsSelected()
    }
}

private object TaskTypeDiffCallback : DiffUtil.ItemCallback<TaskTypeModel>() {
    override fun areItemsTheSame(oldItem: TaskTypeModel, newItem: TaskTypeModel): Boolean {
        return (oldItem.name == newItem.name)//.log("areItemsTheSame")
    }

    override fun areContentsTheSame(oldItem: TaskTypeModel, newItem: TaskTypeModel): Boolean {
        return (oldItem == newItem)//.log("areContentsTheSame")
    }
}

private fun<T> T.log(msj: String? = null) = apply {
    Log.i("TaskTypeAdapter", "${if (msj != null) "$msj: " else ""}${toString()}")
}

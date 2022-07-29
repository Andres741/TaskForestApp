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
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.domain.models.SimpleTaskTypeNameOwner
import com.example.taskscheduler.domain.models.equalsType
import com.example.taskscheduler.ui.main.adapters.bindingAdapters.setSelected
import com.example.taskscheduler.util.OnClickType

class TaskTypeAdapter (
    private val onClickCallBack: OnClickType,
): PagingDataAdapter<TaskTypeModel, TaskTypeViewHolder>(TaskTypeDiffCallback) {

    init {
        TaskTypeViewHolder.removeSelected()
    }

    private val bindViewHolderMap: MutableMap<SimpleTaskTypeNameOwner, TaskTypeViewHolder> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskTypeViewHolder.create(
        parent, onClickCallBack
    )

    override fun onBindViewHolder(holder: TaskTypeViewHolder, position: Int) {
        getItem(position)?.also { taskType ->
            holder.bind(taskType)
            bindViewHolderMap[taskType.toSimpleTaskTypeNameOwner()] = holder
        }
    }

    override fun onViewRecycled(holder: TaskTypeViewHolder) {
        bindViewHolderMap.remove(holder.taskTypeInBinding!!.toSimpleTaskTypeNameOwner())
        super.onViewRecycled(holder)
    }

    fun selectViewHolder(taskType: ITaskTypeNameOwner) {
        TaskTypeViewHolder.selectedTaskType = taskType.log("TaskTypeNameOwner")
        bindViewHolderMap[taskType.toSimpleTaskTypeNameOwner()]?.setIsSelected()
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
        var selectedTaskType: ITaskTypeNameOwner? = null

        fun removeSelected() {
            selectedTaskType = null
            selectedViewHolder?.apply { binding.setSelected(false) }
            selectedViewHolder = null
        }

        fun create(parent: ViewGroup, onClickCallBack: OnClickType) = TaskTypeViewHolder(
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
            return selectedTaskType != null && (binding.taskType?.equalsType(selectedTaskType) == true)
        }

    private fun setCallBacks(onClickCallBack: OnClickType) = apply {
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
        binding.setSelected(false)
    }

    fun setIsSelected() {
        if (isSelected) {
            selectedViewHolder?.unselect()
            binding.setSelected(true)
            selectedViewHolder = this
            return
        }
        binding.setSelected(false)
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

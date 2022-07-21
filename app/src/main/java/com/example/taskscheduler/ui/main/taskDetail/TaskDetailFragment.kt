package com.example.taskscheduler.ui.main.taskDetail

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentTaskDetailBinding
import com.example.taskscheduler.databinding.SaveChangesPopOpWindowBinding
import com.example.taskscheduler.domain.models.equalsType
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapter
import com.example.taskscheduler.util.CallbackAndName
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskDetailFragment: Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()


    private val adapter by lazy { TasksAdapter(tasksAdapterViewModel) }

    private val collectPagingDataScopeProvider = OneScopeAtOnceProvider()

    private var isTitleSaved = true
    private var isTypeSaved = true
    private var isDescriptionSaved = true

    private val saveInHierMsj by lazy { resources.getString(R.string.save_in_hierarchy) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.onSetUp(tasksAdapterViewModel.taskTitleStack.value!!)

        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.fragment_task_detail, container, false)

        return FragmentTaskDetailBinding.bind(root).let {
            _binding = it
            it.viewmodel = viewModel
            it.tasksAdapterViewModel = tasksAdapterViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.subtasksRcy.adapter = adapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.apply activity@ {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner) { tasksAdapterViewModel.removeFromStack() }
        }

        viewModel.apply {
            title.observe(viewLifecycleOwner) {
                val title = task.value?.title ?: return@observe
                isTitleSaved = setSaveStatusColor(title, it, binding.taskTitle)
            }

            type.observe(viewLifecycleOwner) {
                val type = task.value?.type ?: return@observe
                isTypeSaved = setSaveStatusColor(type, it, binding.taskType)
            }

            description.observe(viewLifecycleOwner) {
                val description = task.value?.description ?: return@observe
                isDescriptionSaved = setSaveStatusColor(description, it, binding.taskDescription)
            }

            taskTitleChangedEvent.observe(viewLifecycleOwner) { newTitle ->
                newTitle ?: return@observe
                tasksAdapterViewModel.changeStackTop(newTitle)
            }

            typeChangedEvent.observe(viewLifecycleOwner) { typeChange -> typeChange ?: return@observe
                val currentType = tasksAdapterViewModel.selectedTaskTypeName.value ?: return@observe
                if (currentType equalsType typeChange.first)
                    tasksAdapterViewModel.selectedTaskTypeName.value = typeChange.second
            }

            taskDeletedEvent.setEvent(viewLifecycleOwner) { areMoreWithType ->
                if (areMoreWithType.not()) {
                    tasksAdapterViewModel.selectedTaskTypeName.apply {
                        if (value != null) value = null
                    }
                }
                tasksAdapterViewModel.removeFromStack()
            }
        }

        tasksAdapterViewModel.apply {
            taskTitleStack.observe(viewLifecycleOwner) { taskTitle ->
                if (taskTitle != null) {
                    viewModel.onSetUp(taskTitle)
                    return@observe
                }
                view.findNavController().popBackStack()
                    .ifFalse { "TaskDetailFragment hasn't back stack.".log() }
            }
            /** Introduces the data into the adapter.*/
            tasksDataFlow.observe(viewLifecycleOwner) { flow ->
                collectPagingDataScopeProvider.newScope.launch {
                    flow.collectLatest(adapter::submitData)
                }
            }

            selectedTaskTypeName.observe(viewLifecycleOwner) { typeName ->
                tasksAdapterViewModel.filters.typeFilterCriteria = typeName
            }
        }

        binding.also {
            it.addSubtaskButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.actionFragmentTaskDetailToAddTaskFragment(
                        tasksAdapterViewModel.taskTitleStack.value?.taskTitle
                    )
                )
            }

            it.taskTitle.setOnClickListener onClick@ {
                if (isTitleSaved) return@onClick

                createSaveWindow(
                    oldValueText = viewModel.task.value!!.title, newValueText = viewModel.title.value!!,
                    onSavePressed = viewModel::saveNewTitle, onDiscardPressed = viewModel::restoreTitle
                )
            }

            it.taskType.setOnClickListener onClick@ {
                if (isTypeSaved) return@onClick

                createSaveWindow(
                    oldValueText = viewModel.task.value!!.type, newValueText = viewModel.type.value!!,
                    onSavePressed = viewModel::saveNewType, onDiscardPressed = viewModel::restoreType,
                    optional = viewModel::saveNewTypeInTaskHierarchy to saveInHierMsj
                )
            }

            it.taskDescription.setOnClickListener onClick@ {
                if (isDescriptionSaved) return@onClick

                createSaveWindow(
                    oldValueText = viewModel.task.value!!.description, newValueText = viewModel.description.value!!,
                    onSavePressed = viewModel::saveNewDescription, onDiscardPressed = viewModel::restoreDescription
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_in_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> deleteOptionsMenu.show()
            R.id.filter_by_done_sub -> filterByDoneMenu.show()
            R.id.all_sub -> tasksAdapterViewModel.allTopStackTaskChildren()
            R.id.immediate_children -> tasksAdapterViewModel.allInTaskSource()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private val filterByDoneMenu by lazy {
        val view = activity!!.findViewById<View>(R.id.filter_by_done_sub)
        PopupMenu(context!!, view).apply {
            menuInflater.inflate(R.menu.filter_by_done_menu, menu)

            setOnMenuItemClickListener setMenu@ {
                tasksAdapterViewModel.filters.doneFilterCriteria = when (it.itemId) {
                    R.id.all_by_done -> null
                    R.id.completed -> true
                    R.id.active -> false
                    else -> return@setMenu false
                }
                true
            }
        }
    }

    private val deleteOptionsMenu by lazy {

        val view = activity!!.findViewById<View>(R.id.menu_delete)
        PopupMenu(context!!, view).apply {
            menuInflater.inflate(R.menu.delete_task_options, menu)

            setOnMenuItemClickListener setMenu@ {
                when (it.itemId) {
                    R.id.only_this -> viewModel.deleteOnlyTopStackTask()
                    R.id.also_sub_tasks -> viewModel.deleteTopStackTaskAndChildren()
                    else -> return@setMenu false
                }
                true
            }
        }
    }

    private fun setSaveStatusColor(savedValue: String, candidateNewValue: String?, textView: TextView): Boolean {
        val isDifferent = savedValue == candidateNewValue
        val color = if (isDifferent) resources.getColor(R.color.black)
        else resources.getColor(R.color.unsaved)

        textView.setTextColor(color)
        return isDifferent
    }

    private fun createSaveWindow(
        oldValueText: String, newValueText: String,
        onSavePressed: ()-> Unit, onDiscardPressed: ()-> Unit,
        optional: CallbackAndName? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val winBinding = SaveChangesPopOpWindowBinding.inflate(layoutInflater)
        dialogBuilder.setView(winBinding.root)
        val popUpWin = dialogBuilder.create()
        popUpWin.show()

        winBinding.apply {
            oldValueCont.text = oldValueText
            newValueCont.text = newValueText

            save.setOnClickListener { onSavePressed(); popUpWin.dismiss() }
            discard.setOnClickListener { onDiscardPressed(); popUpWin.dismiss() }
        }

        optional ?: return

        winBinding.optionalButton.apply {
            visibility = View.VISIBLE
            text = optional.second
            setOnClickListener { optional.first(); popUpWin.dismiss() }
        }
    }

    override fun onDestroyView() {
        collectPagingDataScopeProvider.cancel()
        super.onDestroyView()
        _binding = null
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("TaskDetailFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}


package com.example.taskscheduler.ui.main.taskDetail

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentTaskDetailBinding
import com.example.taskscheduler.databinding.SaveChangesPopOpWindowBinding
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapter
import com.example.taskscheduler.util.CallbackAndName
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.scopes.OneScopeAtOnceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        "On create view".log()

        viewModel.onSetUp(tasksAdapterViewModel.taskTitleStack.value!!)

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
            onUpButtonPressedEvent.setEvent(viewLifecycleOwner) {
                removeFromStack()
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { clearStack() }
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
                    onSavePressed = viewModel::saveNewTitle, viewModel::restoreTitle
                )
            }

            it.taskType.setOnClickListener onClick@ {
                if (isTypeSaved) return@onClick
                createSaveWindow(
                    oldValueText = viewModel.task.value!!.type, newValueText = viewModel.type.value!!,
                    onSavePressed = viewModel::saveNewType, viewModel::restoreType,
                    other = viewModel::saveNewTypeInTaskHierarchy to "save in this hierarchy of tasks" //TODO: extract string resource
                )
            }

            it.taskDescription.setOnClickListener onClick@ {
                if (isDescriptionSaved) return@onClick
                createSaveWindow(
                    oldValueText = viewModel.task.value!!.description, newValueText = viewModel.description.value!!,
                    onSavePressed = viewModel::saveNewDescription, viewModel::restoreDescription
                )
            }
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
        }
    }

    private fun setSaveStatusColor(savedValue: String, candidateNewValue: String?, textView: TextView): Boolean {
        val isDifferent = savedValue == candidateNewValue
        val color = if (isDifferent) {
            resources.getColor(R.color.black)
        } else{
            resources.getColor(R.color.unsaved)
        }
        textView.setTextColor(color)
        return isDifferent
    }

    private fun createSaveWindow(
        oldValueText: String, newValueText: String,
        onSavePressed: ()-> Unit, onDiscardPressed: () -> Unit,
        other: CallbackAndName? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val winBinding = SaveChangesPopOpWindowBinding.inflate(layoutInflater)
        dialogBuilder.setView(winBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        winBinding.oldValueCont.text = oldValueText
        winBinding.newValueCont.text = newValueText

        winBinding.save.setOnClickListener { onSavePressed(); dialog.dismiss() }
        winBinding.discard.setOnClickListener { onDiscardPressed(); dialog.dismiss() }

        if (other == null) return

        winBinding.other.apply {
            visibility = View.VISIBLE
            text = other.second
            setOnClickListener { other.first(); dialog.dismiss() }
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


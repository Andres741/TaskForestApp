package com.example.taskscheduler.ui.main.taskDetail

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
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
import com.example.taskscheduler.util.toSimpleDate
import com.example.taskscheduler.util.ui.DatePickerFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*

@AndroidEntryPoint
class TaskDetailFragment: Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()


    private val adapter by lazy { TasksAdapter(tasksAdapterViewModel) }

    private val collectPagingDataScopeProvider = OneScopeAtOnceProvider()

//    private var isTitleSaved = true
//    private var isTypeSaved = true
//    private var isDescriptionSaved = true
//    private var isAdviseDateSaved = true

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
            saveTitleStatus.observe(viewLifecycleOwner) { status ->
                setSaveStatusColor(status, binding.taskTitle)
            }
            saveTypeStatus.observe(viewLifecycleOwner) { status ->
                setSaveStatusColor(status, binding.taskType)
            }
            saveDescriptionStatus.observe(viewLifecycleOwner) { status ->
                setSaveStatusColor(status, binding.taskDescription)
            }
            saveAdviseDateStatus.observe(viewLifecycleOwner) { status ->
                setSaveStatusColor(status, binding.taskAdviseDate)
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

            it.adviseDateCont.setOnClickListener onClick@ {
                val datePicker = DatePickerFragment.newInstanceMinTomorrow {  _, year, month, day ->
                    viewModel.adviseDate.value = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }.time.time.apply { toSimpleDate().log("Selected date") }
                }
                datePicker.show(activity!!.supportFragmentManager, "datePicker")
            }

            it.quitBt.setOnClickListener onClick@ {
                viewModel.adviseDate.value = null
            }


            it.taskTitle.setOnClickListener onClick@ {
                val msj = when (viewModel.saveTitleStatus.value ?: return@onClick) {
                    is TaskDetailViewModel.SavedStatus.Savable -> {
                        createSaveWindow(
                            oldValueText = viewModel.task.value!!.title, newValueText = viewModel.title.value!!,
                            onSavePressed = viewModel::saveNewTitle, onDiscardPressed = viewModel::restoreTitle
                        )
                        null
                    }
                    is TaskDetailViewModel.SavedStatus.Saved -> {
                        R.string.value_saved
                    }
                    is TaskDetailViewModel.SavedStatus.NotSavable -> {
                        viewModel.restoreTitle()
                        R.string.impossible_save_value
                    }
                }
                msj ?: return@onClick
                Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
            }
            it.taskType.setOnClickListener onClick@ {
                val msj = when (viewModel.saveTypeStatus.value ?: return@onClick) {
                    is TaskDetailViewModel.SavedStatus.Savable -> {
                        createSaveWindow(
                            oldValueText = viewModel.task.value!!.type, newValueText = viewModel.type.value!!,
                            onSavePressed = viewModel::saveNewType, onDiscardPressed = viewModel::restoreType,
                            optional = viewModel::saveNewTypeInTaskHierarchy to saveInHierMsj
                        )
                        null
                    }
                    is TaskDetailViewModel.SavedStatus.Saved -> {
                        R.string.value_saved
                    }
                    is TaskDetailViewModel.SavedStatus.NotSavable -> {
                        viewModel.restoreType()
                        R.string.impossible_save_value
                    }
                }
                msj ?: return@onClick
                Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
            }
            it.taskDescription.setOnClickListener onClick@ {
                val msj = when (viewModel.saveDescriptionStatus.value ?: return@onClick) {
                    is TaskDetailViewModel.SavedStatus.Savable -> {
                        createSaveWindow(
                            oldValueText = viewModel.task.value!!.description, newValueText = viewModel.description.value!!,
                            onSavePressed = viewModel::saveNewDescription, onDiscardPressed = viewModel::restoreDescription
                        )
                        null
                    }
                    is TaskDetailViewModel.SavedStatus.Saved -> R.string.value_saved
                    is TaskDetailViewModel.SavedStatus.NotSavable -> {
                        viewModel.restoreDescription()
                        R.string.impossible_save_value
                    }
                }
                msj ?: return@onClick
                Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
            }
            it.taskAdviseDate.setOnClickListener onClick@ {
                val msj = when (viewModel.saveAdviseDateStatus.value ?: return@onClick) {
                    is TaskDetailViewModel.SavedStatus.Savable -> {
                        val doesNotHaveStr = lazy { getString(R.string.doesn_t_have) }
                        val format = viewModel.adviseDateFormat
                        val oldValue = format.run { format(viewModel.task.value?.adviseDate ?: return@run null) }
                        val newValue = format.run { format(viewModel.adviseDate.value ?: return@run null) }

                        createSaveWindow(
                            oldValueText = oldValue ?: doesNotHaveStr.value, newValueText = newValue ?: doesNotHaveStr.value,
                            onSavePressed = viewModel::saveNewAdviseDate, onDiscardPressed = viewModel::restoreAdviseDate
                        )
                        null
                    }
                    is TaskDetailViewModel.SavedStatus.Saved -> R.string.value_saved
                    is TaskDetailViewModel.SavedStatus.NotSavable -> throw IllegalStateException(
                        "New advise date should not be wrong"
                    )
                }
                msj ?: return@onClick
                Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
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

    private fun setSaveStatusColor(status: TaskDetailViewModel.SavedStatus, textView: TextView) =  when(status) {
        is TaskDetailViewModel.SavedStatus.Saved -> resources.getColor(R.color.black)
        is TaskDetailViewModel.SavedStatus.Savable -> resources.getColor(R.color.unsaved)
        is TaskDetailViewModel.SavedStatus.NotSavable -> resources.getColor(R.color.not_savable)
    }.also { color ->
        textView.setTextColor(color)
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


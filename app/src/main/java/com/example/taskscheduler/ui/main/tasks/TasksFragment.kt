package com.example.taskscheduler.ui.main.tasks

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentTasksBinding
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TaskTypeAdapter
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapter
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.util.coroutines.OneScopeAtOnceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TasksFragment: Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()


    private val tasksAdapter by lazy { TasksAdapter(tasksAdapterViewModel, tasksAdapterViewModel.selectedTaskTypeName::setValue) }
    private val taskTypeAdapter by lazy { TaskTypeAdapter(tasksAdapterViewModel.selectedTaskTypeName::setValue) }

    private val collectPagingDataScopeProvider = OneScopeAtOnceProvider()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_tasks, container, false)

        setHasOptionsMenu(true)

        return FragmentTasksBinding.bind(root).let {
            _binding = it
//            it.viewmodel = viewModel
            it.tasksAdapterViewModel = tasksAdapterViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.tasksRcy.adapter = tasksAdapter
            it.tasksTypeRcy.adapter = taskTypeAdapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            lifecycleScope.launch {
                taskTypeDataFlow.collectLatest(taskTypeAdapter::submitData)
            }

            isShowingOnlyTopSuperTask.observe(viewLifecycleOwner) { onlyTopSuperTasks ->
                if (onlyTopSuperTasks) tasksAdapterViewModel.onlySuperTasksInTaskSource()
                else tasksAdapterViewModel.allInTaskSource()

            }
        }

        tasksAdapterViewModel.apply {
            taskTitleStack.observe(viewLifecycleOwner) { task ->
                task ?: return@observe

                view.findNavController().navigate(
                    TasksFragmentDirections.actionFragmentTasksToFragmentTaskDetail()
                )
            }
            /** Introduces the data into the adapter.*/
            tasksDataFlow.observe(viewLifecycleOwner) { flow ->
                collectPagingDataScopeProvider.newScope.launch {
                    flow.collectLatest(tasksAdapter::submitData)
                }
            }

            selectedTaskTypeName.observe(viewLifecycleOwner) { typeName ->
                tasksAdapterViewModel.filterByType(typeName)
                if (typeName == null) {
                    taskTypeAdapter.unselectViewHolder()
                    return@observe
                }
                taskTypeAdapter.selectViewHolder(typeName)
            }
        }

        binding.also {
            it.newTaskButton.setOnClickListener {
                findNavController().navigate(
                    TasksFragmentDirections.actionFragmentTasksToAddTaskFragment(null)
                )
            }
        }
//        notImplementedToastFactory(context, "none")()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_tasks_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_by_done -> filterByDoneMenu.show()
            R.id.all -> viewModel.isShowingOnlyTopSuperTask.value = false
            R.id.only_super -> viewModel.isShowingOnlyTopSuperTask.value = true
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private val filterByDoneMenu by lazy {
        val view = activity!!.findViewById<View>(R.id.filter_by_done)
        PopupMenu(context!!, view).apply {
            menuInflater.inflate(R.menu.filter_by_done_menu, menu)

            setOnMenuItemClickListener setMenu@ {
                when (it.itemId) {
                    R.id.all_by_done -> tasksAdapterViewModel.filterByIsDone(null)
                    R.id.completed -> tasksAdapterViewModel.filterByIsDone(true)
                    R.id.active -> tasksAdapterViewModel.filterByIsDone(false)
                    else -> return@setMenu false
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        collectPagingDataScopeProvider.cancel()
        super.onDestroyView()
        _binding = null
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("TasksFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

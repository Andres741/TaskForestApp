package com.example.taskscheduler.ui.main.tasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentTasksBinding
import com.example.taskscheduler.ui.adapters.itemAdapters.TasksAdapter
import com.example.taskscheduler.ui.adapters.itemAdapters.TasksAdapterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class TasksFragment: Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: TasksViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()
    //Impossible to initialize here adapter because the view model is not available until onCreateView
//    private var _adapter: TasksAdapter? = null  //This provokes null pinter exception sometimes
    private lateinit var adapter: TasksAdapter

    private var collectPagingDataJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        adapter = TasksAdapter(tasksAdapterViewModel)

        val root = inflater.inflate(R.layout.fragment_tasks, container, false)

        return FragmentTasksBinding.bind(root).let {
            _binding = it
//            it.viewmodel = viewModel
            it.tasksAdapterViewModel = tasksAdapterViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.tasksRcy.adapter = adapter  //TODO: fix the adapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksAdapterViewModel.apply {
            taskStack.observe(viewLifecycleOwner){ task ->
                task ?: return@observe

                view.findNavController().navigate(
                    TasksFragmentDirections.actionTaskFragmentToTaskDetailFragment()
                )
            }

            /** Introduces the data into the adapter.*/
            pagingDataFlow.observe(viewLifecycleOwner) { flow ->
                collectPagingDataJob?.cancel()
                collectPagingDataJob = lifecycleScope.launch {
                    flow.collectLatest(adapter::submitData)  //TODO: fix the adapter
                }
            }
        }

        binding.also {
            it.newTaskButton.setOnClickListener {
                findNavController().navigate(
                    TasksFragmentDirections.actionFragmentTasksToAddTaskFragment(null)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("TasksFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

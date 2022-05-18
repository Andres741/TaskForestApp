package com.example.taskscheduler.ui.main.taskDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentTaskDetailBinding
import com.example.taskscheduler.ui.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.ui.adapters.itemAdapters.TasksAdapter
import com.example.taskscheduler.util.ifFalse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class TaskDetailFragment: Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: TaskDetailViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()


    private lateinit var adapter: TasksAdapter

    private var collectPagingDataJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        adapter = TasksAdapter(tasksAdapterViewModel)

        val root = inflater.inflate(R.layout.fragment_task_detail, container, false)

        return FragmentTaskDetailBinding.bind(root).let {
            _binding = it
//            it.viewmodel = viewModel
            it.tasksAdapterViewModel = tasksAdapterViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.subtasksRcy.adapter = adapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksAdapterViewModel.apply {
            taskStack.observe(viewLifecycleOwner){ task ->
                if (task != null) return@observe

                view.findNavController().popBackStack()
                    .ifFalse { throw Exception("TaskDetailFragment has not back stack.") }
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
            it.addSubtaskButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.actionFragmentTaskDetailToAddTaskFragment(
                        tasksAdapterViewModel.taskStack.value?.title
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Timber.i("${if (msj != null) "$msj: " else ""}${toString()}")
    }
}


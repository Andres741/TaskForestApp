package com.example.taskscheduler.ui.main.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.databinding.FragmentTasksBinding
import com.example.taskscheduler.ui.adapters.fragmentAdapters.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels()

    private val adapter = TasksAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        _binding = FragmentTasksBinding.inflate(inflater, container, false)
//        binding.viewmodel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
//        return binding.root

        val root = inflater.inflate(R.layout.fragment_tasks, container, false)

        return FragmentTasksBinding.bind(root).let {
            _binding = it
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.tasksRcy.adapter = adapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Introduces the data into the adapter.*/
        lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest(adapter::submitData)
        }
        /** The same but with LiveData instead Flow. */
//        viewModel.pagingLiveData.observe(viewLifecycleOwner) {
//            adapter.submitData(lifecycle, it)
//        }


        binding.also {
//            it.tasksRcy.adapter = adapter

            it.newTaskButton.setOnClickListener {
                findNavController().navigate(
                    TasksFragmentDirections.actionTaskFragmentToTaskDetailFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

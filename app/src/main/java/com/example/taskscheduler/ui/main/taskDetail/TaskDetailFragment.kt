package com.example.taskscheduler.ui.main.taskDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentTaskDetailBinding
import com.example.taskscheduler.ui.adapters.fragmentAdapters.TaskAdapterViewModel
import com.example.taskscheduler.ui.adapters.fragmentAdapters.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class TaskDetailFragment: Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private val taskAdapterViewModel: TaskAdapterViewModel by activityViewModels()

    private val adapter = TasksAdapter(taskAdapterViewModel)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        _binding = FragmentSecondBinding.inflate(
//            inflater,  container, false
//        )
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_task_detail, container, false
        )

        val root = inflater.inflate(R.layout.fragment_task_detail, container, false)

        return FragmentTaskDetailBinding.bind(root).let {
            _binding = it
            it.viewmodel = viewModel
            it.taskAdapterViewModel = taskAdapterViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.subtasksRcy.adapter = adapter
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskAdapterViewModel.taskStack.observe(viewLifecycleOwner){ task ->
            //Clarification: The new task is yet in taskAdapterViewModel
            if (task == null) {
                if (!view.findNavController().popBackStack()) throw Exception("TaskDetailFragment has not back stack.")
                return@observe
            }
        }

        /** Introduces the data into the adapter.*/
        lifecycleScope.launch {
            taskAdapterViewModel.pagingDataFlow.collectLatest(adapter::submitData)
        }
        /** The same but with LiveData instead Flow. */
//        viewModel.pagingLiveData.observe(viewLifecycleOwner) {
//            adapter.submitData(lifecycle, it)
//        }


        binding.also {
            it.addSubtaskButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.actionFragmentTaskDetailToAddTaskFragment(taskAdapterViewModel.taskStack.value?.title)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


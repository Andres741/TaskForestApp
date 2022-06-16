package com.example.taskscheduler.ui.main.taskDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentTaskDetailBinding
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapter
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


//    private val viewModel: TaskDetailViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()


    private val adapter by lazy { TasksAdapter(tasksAdapterViewModel) }

    private val collectPagingDataScopeProvider = OneScopeAtOnceProvider()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
            taskStack.observe(viewLifecycleOwner) { task ->
                if (task != null) return@observe

                view.findNavController().popBackStack()
                    .ifFalse { "TaskDetailFragment hasn't back stack.".log() } //{ throw IllegalStateException("TaskDetailFragment has not back stack.") }
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
                        tasksAdapterViewModel.taskStack.value?.title
                    )
                )
            }
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


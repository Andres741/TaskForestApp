package com.example.taskscheduler.ui.main.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels()


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
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            newTaskButton.setOnClickListener {
                findNavController().navigate(
                    TasksFragmentDirections.actionFirstFragmentToSecondFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

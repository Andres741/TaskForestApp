package com.example.taskscheduler.ui.main.addTask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.AddTaskFragmentBinding
import com.example.taskscheduler.util.ifFalse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by viewModels()
    private val args: AddTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.add_task_fragment, container, false)

        viewModel.onCreate(args.supertask)

        return AddTaskFragmentBinding.bind(root).let {
            _binding = it
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskHasBeenSaved.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(context, R.string.new_task_error, Toast.LENGTH_LONG).show()
                view.findNavController().popBackStack().ifFalse { throw Exception("AddTaskFragment has not back stack.")}
            } else {
                Toast.makeText(context, R.string.new_task_saved, Toast.LENGTH_LONG).show()
            }
        }

//        binding.apply {
//        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

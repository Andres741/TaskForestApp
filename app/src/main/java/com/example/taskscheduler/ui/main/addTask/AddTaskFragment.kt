package com.example.taskscheduler.ui.main.addTask

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.AddTaskFragmentBinding
import com.example.taskscheduler.domain.CreateValidTaskUseCase
import com.example.taskscheduler.domain.SaveNewTaskUseCase
import com.example.taskscheduler.ui.main.adapters.itemAdapters.TasksAdapterViewModel
import com.example.taskscheduler.util.ifFalse
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by viewModels()
    private val tasksAdapterViewModel: TasksAdapterViewModel by activityViewModels()

    private val args: AddTaskFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.onCreate(args)

        val root = inflater.inflate(R.layout.add_task_fragment, container, false)

        return AddTaskFragmentBinding.bind(root).let {
            _binding = it
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskHasBeenSaved.observe(viewLifecycleOwner) { response ->
            response ?: throw NullPointerException("response is null.")

            var isSuccessful = false

            val message: Int = when (response) {
                is CreateValidTaskUseCase.Response.Successful -> {
                    if (response !is SaveNewTaskUseCase.SavedTask)
                        throw IllegalStateException ("Task has not been saved")
                    isSuccessful = true
                    R.string.new_task_saved
                }
                is CreateValidTaskUseCase.Response.WrongSuperTask -> throw IllegalStateException (
                    "The super task for some reason does not exists."
                )
                is CreateValidTaskUseCase.Response.WrongTitle -> R.string.new_task_wrong_title
                is CreateValidTaskUseCase.Response.WrongType -> R.string.new_task_wrong_type
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            if (isSuccessful) {
                view.findNavController().popBackStack()
                    .ifFalse { "TaskDetailFragment hasn't back stack.".log() }
            }
        }
        tasksAdapterViewModel.onUpButtonPressedEvent.setEvent(viewLifecycleOwner) {
            view.findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("AddTaskFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }

}

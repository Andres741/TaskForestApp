package com.example.taskscheduler.ui.main.addTask

import android.os.Bundle
import android.util.Log
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
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.util.ifFalse
import com.example.taskscheduler.util.ui.DatePickerFragment
import com.example.taskscheduler.util.ui.DateTimePickerFragment
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

        observeViewModel(view)
        setUpOnClickListeners()
    }

    private fun observeViewModel(view: View) {
        viewModel.apply {
            taskHasBeenSaved.observe(viewLifecycleOwner) { response ->
                response ?: throw NullPointerException("response is null.")

                var isSuccessful = false

                val message: Int = when (response) {
                    is ValidTask -> {
                        if (response !is SavedTask)
                            throw IllegalStateException ("Task has not been saved")
                        isSuccessful = true
                        R.string.new_task_saved
                    }
                    is WrongSuperTask -> throw IllegalStateException (
                        "The super task for some reason does not exists."
                    )
                    is WrongAdviseDate -> R.string.days_go_by
                    is WrongTitle -> R.string.new_task_wrong_title
                    is WrongType -> R.string.new_task_wrong_type
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                if (isSuccessful) {
                    view.findNavController().popBackStack()
                        .ifFalse { "TaskDetailFragment hasn't back stack.".log() }
                }
            }
            notValidAdviseDate.setEvent(viewLifecycleOwner) {
                Toast.makeText(context, R.string.must_select_future_date, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun setUpOnClickListeners() {
        binding.apply {
            taskAdviseDate.setOnClickListener {
                val datePicker = DateTimePickerFragment.newInstanceMinNextMinute (true) { timeDate ->
                    viewModel.setAdviseDate(timeDate)
                }
                datePicker.show(activity!!.supportFragmentManager, "datePicker")
            }
            quitBt.setOnClickListener {
                viewModel.adviseDate.value = null
            }
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

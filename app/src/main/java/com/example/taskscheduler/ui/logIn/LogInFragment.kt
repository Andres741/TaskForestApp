package com.example.taskscheduler.ui.logIn

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.taskscheduler.R
import com.example.taskscheduler.ui.main.MainActivity

class LogInFragment : Fragment() {

    companion object {
        fun newInstance() = LogInFragment()
    }

    private val viewModel: LogInModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }
}


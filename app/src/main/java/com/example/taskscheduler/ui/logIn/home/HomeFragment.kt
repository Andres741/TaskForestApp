package com.example.taskscheduler.ui.logIn.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.taskscheduler.databinding.FragmentHomeBinding
import com.example.taskscheduler.ui.logIn.LogInActivityViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: HomeViewModel by viewModels()
    private val activityViewModel: LogInActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firebase.auth.currentUser?.also {
            activityViewModel.goToMainActivity()
        }

        binding.apply {

            logInButton.setOnClickListener(::toLogIn)

            enterNoLogInButton.setOnClickListener {
                activityViewModel.goToMainActivity()
            }
        }
    }

    private fun toLogIn(view: View) = view.findNavController().navigate(
        HomeFragmentDirections.actionHomeFragmentToLogInFragment()
    )
}

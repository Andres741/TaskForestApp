package com.example.taskscheduler.ui.main.logOut

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.ui.logIn.LogInActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogOutFragment: Fragment() {

//    private val viewModel: LogOutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {  }
        return inflater.inflate(R.layout.fragment_log_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logOut()
//        lifecycleScope.launch {
//            delay(4500)
//            Toast.makeText(context, "I was lying", Toast.LENGTH_LONG).show()
//            view.findNavController().popBackStack()
//        }
    }

    private fun logOut() {
        val activity = requireActivity()
        Toast.makeText(context, R.string.bye, Toast.LENGTH_LONG).show()
        Firebase.auth.signOut()
        startActivity(Intent(activity, LogInActivity::class.java))
        activity.finish()
    }
//    companion object {
//        fun newInstance() = LogOutFragment()
//    }
}

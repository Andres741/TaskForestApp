package com.example.taskscheduler.ui.main.logOut

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.example.taskscheduler.R
import com.example.taskscheduler.ui.logIn.LogInActivity
import com.example.taskscheduler.util.coroutines.await
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
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
        logout()
//        lifecycleScope.launch {
//            delay(4500)
//            Toast.makeText(context, "I was lying", Toast.LENGTH_LONG).show()
//            view.findNavController().popBackStack()
//        }
    }

    private fun logout() {
        "logout".log()

        val activity = activity!!
        val context = context ?: return
        Toast.makeText(context, R.string.bye, Toast.LENGTH_LONG).show()


        lifecycleScope.launch {
            AuthUI.getInstance()
                .signOut(context)
                .await()

            Firebase.auth.currentUser.log("Firebase.auth.currentUser")
            startActivity(Intent(activity, LogInActivity::class.java))
            activity.finish()
        }
    }

    private fun <T> T.log(msj: String? = null) = apply {
        Log.i("LogOutFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

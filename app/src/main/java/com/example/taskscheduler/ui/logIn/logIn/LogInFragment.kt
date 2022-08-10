package com.example.taskscheduler.ui.logIn.logIn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.LogInFragmentBinding
import com.example.taskscheduler.ui.logIn.LogInActivityViewModel
import com.example.taskscheduler.ui.main.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private var _binding: LogInFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogInViewModel by viewModels()
    private val activityViewModel: LogInActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LogInFragmentBinding.inflate(inflater, container, false).run {
        _binding = this
        root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        singInUser()
    }

    private fun singInUser() {
        // If there is no signed in user, launch FirebaseUI
        // Otherwise head to MainActivity
        val currentUser = Firebase.auth
            .apply { uid.log("user uid") }
            .currentUser
            .also { it?.displayName.log("user name") }

        if (currentUser == null) {
            goToSignIn()
        } else {
            activityViewModel.goToMainActivity()
        }
    }

    private fun goToSignIn() {
        "goToSignIn".logd()

        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
        )
        // Sign in with FirebaseUI, see docs for more details:
        // https://firebase.google.com/docs/auth/android/firebaseui
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setAvailableProviders(providers)
            .build()

        val signIn: ActivityResultLauncher<Intent> =
            registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

        signIn.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        "onSignInResult".logd()
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            "Sign in successful!".logd()
            Firebase.auth
                .apply { uid.log("user uid") }
                .currentUser?.displayName.log("user name")

            viewModel.startFirebaseSession()
            activityViewModel.goToMainActivity()
            return
        }

        val response = result.idpResponse

        val noLogInMsj = if (response == null) {
            "Sign in canceled".logw()
            R.string.signing_in_cancelled
        } else {
            "Sign in error: ${response.error}".logw()
            R.string.error_signing_in
        }

        Toast.makeText(
            context,
            noLogInMsj,
            Toast.LENGTH_SHORT
        ).show()

        binding.root.findNavController().popBackStack()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("LogInFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logd(msj: String? = null) = apply {
        Log.d("LogInFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logw(msj: String? = null) = apply {
        Log.w("LogInFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

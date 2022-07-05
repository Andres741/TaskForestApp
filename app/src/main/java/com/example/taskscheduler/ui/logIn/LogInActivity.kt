package com.example.taskscheduler.ui.logIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.taskscheduler.R
import com.example.taskscheduler.ui.main.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LogInFragment.newInstance())
                .commitNow()
        }
    }

    public override fun onStart() {
        super.onStart()

        // If there is no signed in user, launch FirebaseUI
        // Otherwise head to MainActivity
        Firebase.auth.currentUser?.displayName.log("Firebase.auth.currentUser")

        if (Firebase.auth.currentUser == null) {
            goToSignIn()
        } else {
            goToMainActivity()
        }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            "Sign in successful!".logd()
            goToMainActivity()
            return
        }

        Toast.makeText(
            this,
            "There was an error signing in",
            Toast.LENGTH_LONG
        ).show()

        val response = result.idpResponse
        if (response == null) {
            "Sign in canceled".logw()
        } else {
            "Sign in error ${response.error}".logw()
        }

    }

    private fun goToSignIn() {
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

        signIn.launch(signInIntent)
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("LogInActivity", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logd(msj: String? = null) = apply {
        Log.d("LogInActivity", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
    private fun<T> T.logw(msj: String? = null) = apply {
        Log.w("LogInActivity", "${if (msj != null) "$msj: " else ""}${toString()}")
    }

}

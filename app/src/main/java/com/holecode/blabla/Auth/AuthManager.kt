package com.holecode.blabla.Auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.holecode.blabla.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthManager(private val activity: FragmentActivity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInLauncher: ActivityResultLauncher<Intent>

    //Add a method to register a new user
    suspend fun registerUser(email: String, password: String): Unit = withContext(Dispatchers.IO) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(true)

        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    // Add a method to forgetPassword user.
    suspend fun forgetPassword(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    //    Add a method to log out the currently authenticated user
    suspend fun logoutUser(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//              handleGoogleSignInResult(task)
        }
    }

    suspend fun authenticateWithGoogle(): Boolean {
        val gso =null
        val googleSignInClient = gso?.let { GoogleSignIn.getClient(activity, it) }
        val signInIntent = googleSignInClient?.signInIntent

        try {
            googleSignInLauncher.launch(signInIntent)
            return true
        } catch (e: Exception) {
            // Handle launch error
            return false
        }
    }

      private suspend fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
          try {
              val account = task.getResult(ApiException::class.java)
              val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
              auth.signInWithCredential(credential).await()
              Result.Success(true)
          } catch (e: ApiException) {
              Result.Error(exception = e)
          }
      }


    //    private fun setupForgetPasswordButton() {
//        val buttonForgetPassword = findViewById<Button>(R.id.buttonForgetPassword)
//
//        buttonForgetPassword.setOnClickListener {
//            lifecycleScope.launch {
//                val result = authManager.resetPassword(email)
//                when (result) {
//                    is Result.Success -> {
//                        // Password reset email sent successfully
//                        val message = result.data
//                        Toast.makeText(this@YourActivity, message, Toast.LENGTH_SHORT).show()
//
//                        // Open Gmail app
//                        val intent = Intent(Intent.ACTION_VIEW).apply {
//                            data = Uri.parse("https://mail.google.com")
//                        }
//
//                        if (intent.resolveActivity(packageManager) != null) {
//                            startActivity(intent)
//                        }
//                    }
//                    is Result.Error -> {
//                        // Password reset email failed
//                        val errorMessage = result.exception
//                        Toast.makeText(this@YourActivity, errorMessage, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }

}
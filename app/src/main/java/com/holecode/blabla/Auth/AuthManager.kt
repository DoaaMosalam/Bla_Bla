package com.holecode.blabla.Auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthManager : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var callbackManager: CallbackManager


    //Add a method to register a new user
    suspend fun registerUser(email: String, password: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                sendEmailVerification()
                when (val result = registerUser(email, password)) {
                    is Result.Success -> {
                        val message = result.toString()
                        Toast.makeText(this@AuthManager, message, Toast.LENGTH_SHORT)
                            .show()
                        // Open Gmail app
                        openGmail()
                    }

                    is Result.Error -> {
                        // Password reset email failed
                        val errorMessage = result.toString()
                        Toast.makeText(this@AuthManager, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                openGmail()
                Result.Success(true)

            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    // add a method to  Send email verification.
    suspend fun sendEmailVerification() {
        val user = auth.currentUser
        try {
            user?.sendEmailVerification()?.await()
            if (user?.isEmailVerified == true) {
                Result.Success(true)
            }
        } catch (e: Exception) {
            throw e

        }
    }

    // Add a method to forgetPassword user.
    suspend fun forgetPassword(email: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email)
                when (val result = forgetPassword(email)) {
                    is Result.Success -> {
                        val message = result.toString()
                        Toast.makeText(this@AuthManager, message, Toast.LENGTH_SHORT)
                            .show()
                        // Open Gmail app
                        openGmail()
                    }
                    is Result.Error -> {
                        // Password reset email failed
                        val errorMessage = result.toString()
                        Toast.makeText(this@AuthManager, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                openGmail()
                Result.Success(true)
            } catch (e: Exception) {
                Result.Error(e)

            }
        }


//Add method to open gmail.
    private fun openGmail() {
        lifecycleScope.launch {
            try {
                // Open Gmail app
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://mail.google.com")
                }

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }

        }

    }

//    Add a method to log out the currently authenticated user
//    suspend fun logoutUser(): Result<Boolean> = withContext(Dispatchers.IO) {
//        try {
//            auth.signOut()
//            Result.Success(true)
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }
}
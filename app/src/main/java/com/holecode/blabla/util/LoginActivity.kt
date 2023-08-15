package com.holecode.blabla.util

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.holecode.blabla.AuthManager
import com.holecode.blabla.HomeActivity
import com.holecode.blabla.Result
import com.holecode.blabla.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var  authManager:AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authManager = AuthManager(this)
        // method o register new user authentication.
        binding.apply {
            binding.btnLogin.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main){
                    if (binding.edEmail.text.toString().trim().isEmpty()) {
                        binding.edEmail.setError("Email is Request")
                        binding.edPassword.requestFocus()
                    } else if (binding.edPassword.text.toString().isEmpty()) {
                        binding.edPassword.setError("Password is Request")
                        binding.edPassword.requestFocus()
                    } else {
                        // call method registerUser from class AuthManager
                        authManager.registerUser(
                            binding.edEmail.text.toString(),
                            binding.edPassword.text.toString()
                        )

                        startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                    }
                }
            }

            //method to forgetPassword user.
            binding.btnForgotPassword.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    // call method forgetPassword from class AuthManager
                    val result = authManager.forgetPassword(binding.edEmail.text.toString())
                    when(result){
                        is Result.Success ->{
                            val message = result.toString()
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                            // Open Gmail app
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://mail.google.com")
                            }
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            }
                        }
                        is Result.Error -> {
                            // Password reset email failed
                            val errorMessage = result.toString()
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://mail.google.com")
                    }

                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }
            //                val googleSignInButton = findViewById<Button>(R.id.btn_google_sign_in)
//                googleSignInButton.setOnClickListener {

            binding.btnGmail.setOnClickListener{
                GlobalScope.launch(Dispatchers.Main) {
                    authManager.authenticateWithGoogle()
                }
            }

        }




//    override fun onStart() {
//        super.onStart()
//        if (authManager!=null){
//         startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
//        }
//    }

    }

}
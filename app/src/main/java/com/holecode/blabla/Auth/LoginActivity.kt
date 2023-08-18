package com.holecode.blabla.Auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.holecode.blabla.HomeActivity
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authManager = AuthManager()
        // Initialize Facebook Login
        callbackManager = CallbackManager.Factory.create()

        // method o register new user authentication.
        binding.apply {
            binding.btnLogin.setOnClickListener {
                lifecycleScope.launch {
                    if (binding.edEmail.text.toString().trim().isEmpty()) {
                        binding.edEmail.error = "Email is Request"
                        binding.edPassword.requestFocus()
                    } else if (binding.edPassword.text.toString().isEmpty()) {
                        binding.edPassword.error = "Password is Request"
                        binding.edPassword.requestFocus()
                    } else {
                        // call method registerUser from class AuthManager
                        authManager.registerUser(
                            binding.edEmail.text.toString(),
                            binding.edPassword.text.toString())
                        authManager.sendEmailVerification()
                    }
                    val result = authManager.registerUser(binding.edEmail.text.toString(), binding.edPassword.text.toString())
                    when (result) {
                        is Result.Success -> {
                            val message = result.toString()
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT)
                                .show()
                            // Open Gmail app
                            openGmail()
                        }

                        is Result.Error -> {
                            // Password reset email failed
                            val errorMessage = result.toString()
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    openGmail()
                }
            }
            //method to forgetPassword user.
            binding.btnForgotPassword.setOnClickListener {
                lifecycleScope.launch {
                    // call method forgetPassword from class AuthManager
                    val result = authManager.forgetPassword(binding.edEmail.text.toString())
                    when (result) {
                        is Result.Success -> {
                            val message = result.toString()
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT)
                                .show()
                            // Open Gmail app
                            openGmail()
                        }

                        is Result.Error -> {
                            // Password reset email failed
                            val errorMessage = result.toString()
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    openGmail()
                }
            }
            binding.btnGmail.setOnClickListener {
                lifecycleScope.launch {
                    authenticateWithGoogle()
                }
            }
            binding.btnFacebook.setOnClickListener {
                lifecycleScope.launch {
                    authenticationWithFacebook()
                }
            }

        }
    }

    // Add method to intent between this class anther class
    private fun navigateToHomePage() {
        lifecycleScope.launch {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

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

    // Add method to authentication with mail by google
    private fun authenticateWithGoogle() {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((getString(R.string.default_web_client_id))) // Your Web Client ID from Firebase Console
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(this, option)
        signInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // Add method to authentication with facebook
    private fun authenticationWithFacebook() {
        callbackManager = CallbackManager.Factory.create()
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            navigateToHomePage()
        }
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {

                }

                override fun onSuccess(result: LoginResult) {
                    navigateToHomePage()
                }
            })
        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    //Add method to start home page when user finish auth.
//    override fun onStart() {
//        super.onStart()
//
//        navigateToHomePage()
//    }
}

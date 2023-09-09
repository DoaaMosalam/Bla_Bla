package com.holecode.blabla.Auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivityLoginBinding
import com.holecode.blabla.setting.ProfileActivity
import com.holecode.blabla.setting.SetUpFirebase
import com.holecode.blabla.util.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class LoginActivity : AppCompatActivity(), TextWatcher,SetUpFirebase {
    private lateinit var binding: ActivityLoginBinding
    lateinit var authManager: AuthManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var checkIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkIcon = ContextCompat.getDrawable(this, R.drawable.baseline_check_24)!!
//==================================================================================================
        binding.apply {
            binding.edEmail.addTextChangedListener(this@LoginActivity)
            binding.edPassword.addTextChangedListener(this@LoginActivity)
        }
        setEmailFocusListener()
        setPasswordFocusListener()

        authManager = AuthManager()
        // Initialize Facebook Login
        callbackManager = CallbackManager.Factory.create()
//==================================================================================================

        // handle on click button(login,forget,google,facebook)
        binding.apply {
            binding.btnLogin.setOnClickListener {
                lifecycleScope.launch {
                    val email = binding.edEmail.text.toString().trim()
                    val password = binding.edPassword.text.toString().trim()
                    authManager.registerUser(email, password).apply {
                        val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }

            }

            //method to forgetPassword user.
            binding.btnForgotPassword.setOnClickListener {
                lifecycleScope.launch {
                    // call method forgetPassword from class AuthManager
                    authManager.forgetPassword(binding.edEmail.text.toString().trim())
                }
            }
            // Handle set on click button sign up google
            binding.btnGmail.setOnClickListener {
                lifecycleScope.launch {
                    authenticateWithGoogle()
                }
            }
            // Handle set on click button sign up facebook
            binding.btnFacebook.setOnClickListener {
                lifecycleScope.launch {
                    authenticationWithFacebook()
                }
            }

        }
    }
    override val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override val firebaseStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override val dataBase: FirebaseDatabase
        get() = TODO("Not yet implemented")
    override val storage: FirebaseStorage
        get() = TODO("Not yet implemented")

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        binding.btnLogin.isEnabled =
            binding.edEmail.text!!.trim().isNotEmpty()
                    && binding.edPassword.text.toString().trim().isNotEmpty()
                    && validateEmail()
                    &&validatePassword()
    }

    private fun validateEmail():Boolean{
        val valueEmail = binding.edEmail.text.toString().toString()
        if (valueEmail.isEmpty()){
          binding.emailTil.error="Email is Required"
            binding.emailTil.endIconDrawable = null
        }else if (!isValidEmail(valueEmail)){
            binding.emailTil.error="Invalid email address"
            binding.emailTil.endIconDrawable=null
        }else{
            binding.emailTil.apply {
                error=null
                endIconDrawable=checkIcon
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))

            }
        }
        return binding.emailTil.error==null
    }

    private fun validatePassword():Boolean{
        val valuePassword= binding.edPassword.text.toString().trim()
        if (valuePassword.isEmpty()){
            binding.passwordTil.error = "Password is require"
            binding.passwordTil.endIconDrawable = null
        }else if (valuePassword.length < 6) {
            binding.passwordTil.error = "Password must be at least 6 characters"
            binding.passwordTil.endIconDrawable = null
        } else if (!valuePassword.matches(".*[A-Z].*".toRegex())) {
            binding.passwordTil.error =
                "Password must contain 1 upper-case character"
            binding.passwordTil.endIconDrawable = null
        } else if (!valuePassword.matches(".*[a-z].*".toRegex())) {
            binding.passwordTil.error =
                "Password must contain 1 lower-case character"
        } else if (!valuePassword.matches(".*[@#\$%^&+=].*".toRegex())) {
            binding.passwordTil.error =
                "Password must contain special[@#\$%^&+=] "
        } else {
            binding.passwordTil.apply {
                error = null
                endIconDrawable = checkIcon
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.passwordTil.error == null
    }
    //this method validate email when login
    private fun isValidEmail(email:String):Boolean{
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(pattern.toRegex())
    }

//this method setFocusedListener email appear require when login
    private fun setEmailFocusListener(){
        val emailValue = binding.edEmail
        emailValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }
    //this method setFocusedListener password appear require when login
    private fun setPasswordFocusListener(){
        val passwordValue = binding.edPassword
        passwordValue.setOnFocusChangeListener{_,hasFocuse ->
            if (!hasFocuse){
                validatePassword()
            }
        }
    }

    //==================================================================================================
    // Add method to authentication with mail by google
    private fun authenticateWithGoogle() {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((getString(R.string.default_web_client_id))) // Your Web Client ID from Firebase Console
            .requestEmail().build()
        val signInClient = GoogleSignIn.getClient(this, option)
        signInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
        }
    }

    private suspend fun googleAuthForFirebase(account: GoogleSignInAccount): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            try {
                auth.signInWithCredential(credential).await()
                Result.Success(true)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@AuthManager, e.message, Toast.LENGTH_SHORT).show()
                    Result.Error(e)
                }
            }

        }

    // Add method to authentication with facebook
    private suspend fun authenticationWithFacebook() {
        callbackManager = CallbackManager.Factory.create()
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            navigateToHomePage()
        }
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
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
                lifecycleScope.launch {
                    googleAuthForFirebase(it)
                }

            }
        }
    }

    //==================================================================================================
    // Add method to intent between this class anther class
    private fun navigateToHomePage() {
        lifecycleScope.launch {
            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



//==================================================================================================

    //Add method to start home page when user finish auth.
//    override fun onStart() {
//        super.onStart()
//        navigateToHomePage()
//    }


}
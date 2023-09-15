package com.holecode.blabla.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.Auth.LoginActivity
import com.holecode.blabla.databinding.ActivityAccountBinding
import kotlinx.coroutines.launch

class AccountActivity : AppCompatActivity(), SetUpFirebase {
    //    override val bindingInflater: (LayoutInflater) -> ActivityAccountBinding = ActivityAccountBinding::inflate
    private lateinit var binding: ActivityAccountBinding
    override val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override val firebaseStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override val dataBase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()

    }
    override val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSingOut.setOnClickListener {
            signOutUser()
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this@AccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun signOutUser() {
        lifecycleScope.launch {
            val user = auth.currentUser?.uid
            if (user != null) {
                auth.signOut()
            } else {
                navigateToHomePage()
            }


        }
    }
}
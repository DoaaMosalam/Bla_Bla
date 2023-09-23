package com.holecode.blabla.setting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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
    override val database: FirebaseDatabase by lazy {
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
        /*handle button back by toolbar*/
        val toolbar = binding.txtAccount
        toolbar.text="Account"
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.title=""
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    } //end onCreate

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
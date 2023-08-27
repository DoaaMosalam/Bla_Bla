package com.holecode.blabla.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.holecode.blabla.Auth.AuthManager
import com.holecode.blabla.Auth.LoginActivity
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivityAccountBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var  auth:AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSingOut.setOnClickListener{
            lifecycleScope.launch(Dispatchers.Main){
                auth.logoutUser()
                startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
            }
        }
    }
}
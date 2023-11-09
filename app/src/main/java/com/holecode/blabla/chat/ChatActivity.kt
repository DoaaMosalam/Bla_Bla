package com.holecode.blabla.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.databinding.ActivityChatBinding
import com.holecode.blabla.setting.SetUserFirebase

class ChatActivity : AppCompatActivity() {
    private lateinit var bindingChatActivity: ActivityChatBinding

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingChatActivity = ActivityChatBinding.inflate(layoutInflater)
        setContentView(bindingChatActivity.root)
        val name = intent.getStringExtra("user_name")
        val profileImage = intent.getStringExtra("profile_image")
        bindingChatActivity.txtNameUserChat.text = name
        SetUserFirebase.getUserInfo { user ->
            if (user.imageUrl.isNotEmpty()) {
                Glide.with(this@ChatActivity)
                    .load(user.imageUrl)
                    .into(bindingChatActivity.chatImage)
            }
        }
        bindingChatActivity.backImage.setOnClickListener {
            finish()
        }
    }//end onCreate
}
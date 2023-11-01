package com.holecode.blabla.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivityChatBinding
import com.holecode.blabla.fragbottomnav.ChatsFragment
import com.holecode.blabla.setting.SetUserFirebase

class ChatActivity : AppCompatActivity() {
    private lateinit var bindingChatActivity: ActivityChatBinding

    private lateinit var userName: String
    private lateinit var userstatus: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingChatActivity = ActivityChatBinding.inflate(layoutInflater)
        setContentView(bindingChatActivity.root)
//        val toolbar = bindingChatActivity.txtInfoUserChat
//        toolbar.text = ""
//        setSupportActionBar(bindingChatActivity.toolbarChat)
//        supportActionBar?.title = ""
//        setSupportActionBar(bindingChatActivity.toolbarChat)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)

        SetUserFirebase.getUserInfo { user ->
            userName = user.name
            bindingChatActivity.txtNameUserChat.setText(userName)

            if (user.imageUrl.isNotEmpty()) {
                val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.baseline_image_24) // Placeholder image resource
                    .error(R.drawable.baseline_error_24) // Error image resource

                Glide.with(this@ChatActivity)
                    .load(user.imageUrl)
                    .apply(requestOptions)
                    .into(bindingChatActivity.chatImage)
            }
        }
        bindingChatActivity.backImage.setOnClickListener {
//            startActivity(Intent(this@ChatActivity,ChatsFragment::class.java))
            finish()
        }
    }//end onCreate

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
package com.holecode.blabla.fragbottomnav


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.holecode.blabla.R
import com.holecode.blabla.chat.ChatActivity
import com.holecode.blabla.chat.ChatItemAdapter
import com.holecode.blabla.chat.ChatUser
import com.holecode.blabla.databinding.ChatItemBinding
import com.holecode.blabla.databinding.FragmentChatsBinding
import com.holecode.blabla.pojo.UserProfile
import com.holecode.blabla.setting.SetUserFirebase
import com.xwray.groupie.GroupAdapter

import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
//import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener


class ChatsFragment : Fragment() {
private lateinit var bindingChat:FragmentChatsBinding
    private lateinit var chatSection :Section

    private lateinit var userName: String
    private lateinit var userMessage: String
    private val firebaseStoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingChat = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        addChatListener(::initRecyclerView) // Call addChatListener before returning the view
        //=======================================================================================

        return bindingChat.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val imageCircle = view.findViewById<ImageView>(R.id.image_itemcircle)
//        SetUserFirebase.getUserInfo { user ->
//            userName = user.name
//            if (user.imageUrl.isNotEmpty()) {
//                val requestOptions = RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.baseline_image_24) // Placeholder image resource
//                    .error(R.drawable.baseline_error_24) // Error image resource
//
//                Glide.with(requireActivity())
//                    .load(user.imageUrl)
//                    .apply(requestOptions)
//                    .error(R.drawable.error_outline_24)
//                    .into(imageCircle!!)
//            }
//        }
    }

// Listening to chats...
private fun addChatListener(onListen: (List<Item<*>>) -> Unit): ListenerRegistration {
    return firebaseStoreInstance.collection("users")
        .addSnapshotListener { querySnap, firebaseFireException ->
            if (firebaseFireException != null) {
                // Handle the error here if needed
                return@addSnapshotListener
            }
            val items = mutableListOf<Item<*>>() // Create a list of Groupie items

            querySnap?.documents?.forEach { document ->
                val chatUser = document.toObject(ChatUser::class.java)
                if (chatUser != null) {
                    items.add(ChatItemAdapter(chatUser, requireContext()))
                }
            }
            onListen(items)
        }
}

    private fun initRecyclerView(items: List<Item<*>>) {
        bindingChat.chatRecyclerView.apply {
            setHasFixedSize(true) // For performance optimization
            layoutManager = LinearLayoutManager(requireContext())
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                chatSection = Section(items)
                add(chatSection)
                setOnItemClickListener(onItemClick)
            }
        }
    }
    val onItemClick = OnItemClickListener{item,view ->
        startActivity(Intent(activity,ChatActivity::class.java))
    }
}


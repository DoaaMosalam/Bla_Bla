package com.holecode.blabla.fragbottomnav


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.holecode.blabla.R
import com.holecode.blabla.chat.ChatActivity
import com.holecode.blabla.chat.ChatItemAdapter
import com.holecode.blabla.chat.ChatUser
import com.holecode.blabla.databinding.FragmentChatsBinding
import com.holecode.blabla.setting.SetUserFirebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section

class ChatsFragment : Fragment() {
    private lateinit var bindingChat: FragmentChatsBinding
    private lateinit var chatSection: Section

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
    }

    // Listening to chats...
    private fun addChatListener(onListen: (List<Item<*>>) -> Unit): ListenerRegistration {
        return SetUserFirebase.firebaseStoreInstance.collection("users")
            .addSnapshotListener { querySnap, firebaseFireException ->
                if (firebaseFireException != null) {
                    // Handle the error here if needed
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item<*>>() // Create a list of Groupie items

                querySnap?.documents?.forEach { document ->
                    val chatUser = document.toObject(ChatUser::class.java)
                    if (chatUser != null) {
                        // Fetch user image URL along with user information
                        SetUserFirebase.getUserInfo { userProfile ->
                            val imageUrl = userProfile.imageUrl
                            items.add(ChatItemAdapter(chatUser, imageUrl, requireContext()))
                            onListen(items)
                        }
                    }
                }
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

    val onItemClick = OnItemClickListener { item, view ->
        if (item is ChatItemAdapter) {
            val name = item.user.name
            val image = item.user.image
            // Handle item click, e.g., start a chat activity with the selected user
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("user_name", name)
            intent.putExtra("profile_image", image)
            startActivity(intent)
        }
        true
    }

}


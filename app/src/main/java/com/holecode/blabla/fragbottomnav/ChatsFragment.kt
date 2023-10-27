package com.holecode.blabla.fragbottomnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.holecode.blabla.R
import com.holecode.blabla.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {
private lateinit var bindingChat:FragmentChatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingChat = DataBindingUtil.inflate(inflater,R.layout.fragment_chats,container,false)
        return bindingChat.root
    }
}
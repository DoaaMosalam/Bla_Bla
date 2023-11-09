package com.holecode.blabla.chat

import android.content.Context
import com.bumptech.glide.Glide
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ChatItemBinding
import com.xwray.groupie.databinding.BindableItem


class ChatItemAdapter(
    val user: ChatUser,
    private val imageUrl: String?,
    private val context: Context
) :
    BindableItem<ChatItemBinding>() {
    override fun bind(viewBinding: ChatItemBinding, position: Int) {
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.error_outline_24)
                .into(viewBinding.imageItemcircle)
        } else {
            viewBinding.imageItemcircle.setImageResource(R.drawable.baseline_image_24)
        }
        viewBinding.nameItem.text = user.name
        viewBinding.messageItem.text = "Last Message"
        viewBinding.timeItem.text = user.formattedTime()
    }

    override fun getLayout(): Int {
        return R.layout.chat_item
    }
}

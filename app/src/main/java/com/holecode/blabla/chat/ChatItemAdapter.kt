package com.holecode.blabla.chat


import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ChatItemBinding
import com.holecode.blabla.pojo.UserProfile
import com.holecode.blabla.setting.SetUserFirebase
import com.xwray.groupie.databinding.BindableItem
//import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
//import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date


class ChatItemAdapter(private val user:ChatUser,private val context:Context):BindableItem<ChatItemBinding>(){
    private val storageInstance:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewBinding: ChatItemBinding, position: Int) {

        if (user.image.isNotEmpty()) {
            Glide.with(viewBinding.imageItemcircle)
                .load(storageInstance.getReference(user.image))
                .into(viewBinding.imageItemcircle)
        }
        else{
            viewBinding.imageItemcircle.setImageResource(R.drawable.person_24)
        }
            viewBinding.nameItem.text = user.name
            viewBinding.messageItem.text = "Last Message"
            viewBinding.timeItem.text = user.formattedTime()
        }

    override fun getLayout(): Int {
       return R.layout.chat_item
    }


}
// fun retrieveImage(){
//     val databaseReference = FirebaseDatabase.getInstance().getReference("users")
//
//     databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
//         override fun onDataChange(dataSnapshot: DataSnapshot) {
//             val user = dataSnapshot.getValue(ChatUser::class.java)
//             if (user != null) {
//                 // Retrieve the image URL from the user data
//                 val imageUrl = user.image
//
//                 // Use Glide to load and display the image
//                 Glide.with(context)
//                     .load(imageUrl)
//                     .into(viewBinding.imageItemcircle)
//             }
//         }
//
//         override fun onCancelled(databaseError: DatabaseError) {
//             // Handle errors, if any
//         }
//     })
// }

//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        val context = viewHolder.itemView.context
//
//        val imageItemcircle = viewHolder.itemView.findViewById<ImageView>(R.id.image_itemcircle)
//        val nameItem = viewHolder.itemView.findViewById<TextView>(R.id.name_item)
//        val messageItem = viewHolder.itemView.findViewById<TextView>(R.id.message_item)
//        val timeItem = viewHolder.itemView.findViewById<TextView>(R.id.time_item)
//
//        if (user.image.isNotEmpty()) {
//            Glide.with(context)
//                .load(storageInstance.getReference(user.image))
//                .placeholder(R.drawable.person_24)
//                .error(R.drawable.error_outline_24)
//                .into(imageItemcircle)
//        }else{
//            imageItemcircle.setImageResource(R.drawable.person_24)
//
//        }
//
//        nameItem.text = user.name ?: "Unknown User"
//        messageItem.text = "Last message..."
//          timeItem.text = user.formattedTime()
//    }
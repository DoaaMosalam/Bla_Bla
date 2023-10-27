package com.holecode.blabla.setting

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holecode.blabla.pojo.UserProfile

object SetUserInfo {
     val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

     val firebaseStoreInstance: FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }
     val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
     val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    val currentUserDocRef: DocumentReference
        get() = firebaseStoreInstance.collection("users")
            .document(auth.currentUser?.uid.toString())

     val currentUserStorageRef: StorageReference
        get() = storage.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    fun getUserInfo(onComplete: (UserProfile) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserProfile::class.java)
                user?.let { onComplete(it) }
            }
        }
    }
}
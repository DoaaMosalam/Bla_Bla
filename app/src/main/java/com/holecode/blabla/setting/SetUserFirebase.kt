package com.holecode.blabla.setting


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holecode.blabla.pojo.UserProfile

object SetUserFirebase {
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseStoreInstance: FirebaseFirestore by lazy {
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
        get() = storage.reference.child("profile_images/${auth.currentUser?.uid.toString()}")

    /*Method return info from FireStore Database
       * used UserProfile.class*/
    fun getUserInfo(onComplete: (UserProfile) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserProfile::class.java)
                user?.let {
                    currentUserStorageRef.getDownloadUrl().addOnSuccessListener { uri ->
                        user.imageUrl = uri.toString()
                        onComplete(user)
                    }
                }
            }
        }
    }

//    fun getUserInfo(onComplete: (UserProfile) -> Unit) {
//        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.exists()) {
//                val user = documentSnapshot.toObject(UserProfile::class.java)
//                user?.let { onComplete(it) }
//            }
//        }
//    }
}
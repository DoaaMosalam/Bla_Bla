package com.holecode.blabla.setting

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

interface SetUpFirebase {
     val auth: FirebaseAuth
    val firebaseStoreInstance:FirebaseFirestore
    val dataBase:FirebaseDatabase
    val storage:FirebaseStorage
}
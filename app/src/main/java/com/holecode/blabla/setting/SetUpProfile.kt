package com.holecode.blabla.setting

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

interface SetUpProfile {
     val auth: FirebaseAuth
    val firebaseStoreInstance:FirebaseFirestore
}
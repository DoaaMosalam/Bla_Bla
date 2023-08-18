package com.holecode.blabla.Auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthManager {
    private val auth = FirebaseAuth.getInstance()
    //Add a method to register a new user
    suspend fun registerUser(email: String, password: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                sendEmailVerification()
                Result.Success(true)

            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    // add a method to  Send email verification.
    suspend fun sendEmailVerification() {
        val user = auth.currentUser
        try {
            user?.sendEmailVerification()?.await()
            if (user?.isEmailVerified == true) {
                Result.Success(true)
            }
        } catch (e: Exception) {
            throw e

        }
    }

    // Add a method to forgetPassword user.
    suspend fun forgetPassword(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    //    Add a method to log out the currently authenticated user
//    suspend fun logoutUser(): Result<Boolean> = withContext(Dispatchers.IO) {
//        try {
//            auth.signOut()
//            Result.Success(true)
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }
}







package com.holecode.blabla.setting


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holecode.blabla.R

import com.holecode.blabla.databinding.ActivityProfileBinding
import com.holecode.blabla.pojo.User
import com.holecode.blabla.pojo.UserProfile
import com.holecode.blabla.util.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID


class ProfileActivity : AppCompatActivity(), SetUpFirebase, View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var selectedImage: Uri
    private lateinit var userName: String
    private lateinit var userstatus: String
    private lateinit var dialog: AlertDialog.Builder

    // Constants
    private val PICK_IMAGE_REQUEST = 1

    //=============================================================================================
    override val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override val firebaseStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    override val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
//    private val currentUserDocRef:DocumentReference
//        get() = firebaseStoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val currentUserDocRef: DocumentReference
        get() = firebaseStoreInstance.collection("users")
            .document(auth.currentUser?.uid.toString())

    private val currentUserStorageRef: StorageReference
        get() = storage.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    //=============================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = AlertDialog.Builder(this)
            .setMessage("Update Profile...")
            .setCancelable(false)
        //===========================================================
        binding.btnDone.setOnClickListener(this)
        binding.imageProfile.setOnClickListener(this)
        //=========================================================================================
        /*handle button back by toolbar*/
        val toolbar = binding.txtProfile
        toolbar.text = "Profile"
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.title = ""
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        getUserInfo { user ->
            userName = user.name
            userstatus = user.status
            binding.nameProfile.setText(userName)
            binding.statusProfile.setText(userstatus)
            if (user.imageUrl.isNotEmpty()) {
                val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.baseline_image_24) // Placeholder image resource
                    .error(R.drawable.baseline_error_24) // Error image resource

                Glide.with(this@ProfileActivity)
                    .load(user.imageUrl)
                    .apply(requestOptions)
                    .into(binding.imageProfile)
            }
        }
    } //end onCreate

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //=============================================================================================
    //This method is setOnClickListener.
    override fun onClick(view: View?) {
        if (view != null && view.id == R.id.btn_done) {
            val name = binding.nameProfile.text.toString()
            val status = binding.statusProfile.text.toString()
            if (name.isEmpty()) {
                binding.nameProfile.error = "Please type your name"
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    uploadData()

                }
                startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
            }
        } else if (view != null && view.id == R.id.image_profile) {
            lifecycleScope.launch(Dispatchers.Main) {
                OpenImageChooser()
            }
        }
    }

    //=============================================================================================
    //this method open gallery to choose image.
    suspend fun OpenImageChooser() = withContext(Dispatchers.IO) {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private suspend fun uploadData() = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid.toString()
        val name = binding.nameProfile.text.toString()
        val status = binding.statusProfile.text.toString()

        val user = UserProfile(uid, name, status, "No Image")

        val reference = storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedImage).addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener { downloadUri ->
                    user.imageUrl = downloadUri.toString()
                    lifecycleScope.launch {
                        saveUserProfile(user)
                    }
                }
            } else {
                lifecycleScope.launch {
                    saveUserProfile(user)
                }

            }
        }

//
//        val reference = storage.reference.child("Profile").child(Date().time.toString())
//        reference.putFile(selectedImage).addOnCompleteListener {
//            if (it.isSuccessful) {
//                reference.downloadUrl.addOnSuccessListener { task ->
//                    lifecycleScope.launch {
//                        uploadInfo(task.toString())
//                    }
//                }
//            } else {
//                val uid = auth.uid.toString()
//                val na = binding.nameProfile.text.toString()
//                val about = binding.statusProfile.text.toString()
//                val user = UserProfile(
//                    uid,
//                    na,
//                    about,
//                    "No Image"
//                )
//                // Call retrieveUserData before uploading user data
//                retrieveUserData()
//                database.reference.child("users").child(uid).setValue(user)
//                    .addOnCanceledListener {
//                        val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//            }
//
//        }
    }

    private suspend fun saveUserProfile(user: UserProfile) = withContext(Dispatchers.IO) {
        currentUserDocRef.set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, "Data inserted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Failed to insert data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    suspend fun uploadInfo(imageUri: String) = withContext((Dispatchers.IO)) {
        val user = UserProfile(
            auth.uid.toString(),
            binding.nameProfile.text.toString(),
            binding.statusProfile.text.toString(),
            imageUri
        )
        database.reference.child("users")
            .child(auth.uid.toString())
            .setValue(user)
            .addOnCompleteListener {
                Toast.makeText(this@ProfileActivity, "Data  inserted", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                /*this code compress image
                * save image in firebase realtime*/
                binding.imageProfile.setImageURI(data.data)
                val selectedImagePath = data.data
                val selectedImageBmp =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
                val outPutStream = ByteArrayOutputStream()
                selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 20, outPutStream)
                val selectedImageByte = outPutStream.toByteArray()

                UploadProfileImage(selectedImageByte) { path ->
                    val userMap = mutableMapOf<String, Any>()
                    userMap["name"] = userName
                    userMap["Pictures"] = path
                    currentUserDocRef.update(userMap)

                }

                val uri = data.data //filePath
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference.child("Profile").child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val filePath = uri.toString()
                            val obj = HashMap<String, Any>()
                            obj["image"] = filePath
                            database.reference.child("users")
                                .child(FirebaseAuth.getInstance().uid!!).updateChildren(obj)
                                .addOnSuccessListener { }
                        }
                    }
                }
            }
            binding.imageProfile.setImageURI(data.data)
            selectedImage = data.data!!

        }
    }


    private fun UploadProfileImage(
        selectedImageByte: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref =
            currentUserStorageRef.child("ProfilePictures/${UUID.nameUUIDFromBytes(selectedImageByte)}")
        ref.putBytes(selectedImageByte).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(ref.path)
            } else {
                Toast.makeText(
                    this@ProfileActivity,
                    "Error:${it.exception?.message.toString()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

//    private fun getUserInfo(onComplete: (UserProfile) -> Unit) {
//        currentUserDocRef.get().addOnSuccessListener {
//            onComplete(it.toObject(UserProfile::class.java)!!)
//            user?.let { onComplete(it) }
//        }
//    }


    private fun getUserInfo(onComplete: (UserProfile) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UserProfile::class.java)
                user?.let { onComplete(it) }
            }
        }
    }

    fun retrieveUserData() {
        val uid = auth.uid
        val dbRef = database.reference.child("users").child(auth.uid!!)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserProfile::class.java)
                if (snapshot.exists()) {
                    user?.let {
                        val name = user.name
                        val status = user.status
                        val image = user.imageUrl
                        // Use the retrieved data as needed
                        // For example, you can update the UI with the retrieved values
                        binding.nameProfile.setText(name)
                        binding.statusProfile.setText(status)
                        Glide.with(this@ProfileActivity).load(image).into(binding.imageProfile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}
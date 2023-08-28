package com.holecode.blabla.setting


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.databinding.ActivityProfileBinding
import com.holecode.blabla.pojo.User
import com.holecode.blabla.util.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImage: Uri
    private lateinit var dialog:AlertDialog.Builder

    // Constants
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dialog=AlertDialog.Builder(this)
            .setMessage("Update Profile...")
            .setCancelable(false)
//initialize firebase
        database= FirebaseDatabase.getInstance()
        storage= FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.imageProfile.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                OpenImageChooser()
            }
        }

        binding.btnDone.setOnClickListener {
            val name = binding.nameProfile.text.toString()
            val status = binding.aboutProfile.text.toString()
            if (name.isEmpty()){
                binding.nameProfile.error = "Please type your name"
            } else
                lifecycleScope.launch(Dispatchers.Main){
                    uploadData()
                }
        }

    }

    //this method open gallery to choose image.
    suspend fun OpenImageChooser() = withContext(Dispatchers.IO) {
        val intent = Intent()
        intent.action=Intent.ACTION_GET_CONTENT
        intent.type="image/*"
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }
    suspend fun uploadData() = withContext(Dispatchers.IO) {
            val reference = storage.reference.child("Profile").child(Date().time.toString())
            reference.putFile(selectedImage).addOnCompleteListener {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        lifecycleScope.launch {
                            uploadInfo(task.toString())
                        }
                    }
                }
                else {
                    val uid = auth.uid.toString()
                    val na = binding.nameProfile.text.toString()
                    val about = binding.aboutProfile.text.toString()
                    val user = User(
                        uid,
                        na,
                        about,
                        "No Image"
                    )
                    // Call retrieveUserData before uploading user data


                    database.reference.child("users").child(uid).setValue(user)
                        .addOnCanceledListener {
                            val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }

            }
    }

    suspend fun uploadInfo(imageUri: String) = withContext((Dispatchers.IO)){
        val user = User(auth.uid.toString(),
            binding.nameProfile.text.toString() ,
            binding.aboutProfile.text.toString(),
            imageUri)
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (data != null){
//            if (data.data != null ){
//                selectedImage = data.data!!
//                binding.imageProfile.setImageURI(selectedImage)
//            }
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
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

    private fun retrieveUserData(){

    }

//    override fun onStart() {
//        super.onStart()
//        val user = auth.currentUser!!.uid
//        startActivity(Intent(this@ProfileActivity,HomeActivity::class.java))
//    }

}
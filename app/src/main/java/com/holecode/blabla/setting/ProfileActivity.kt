package com.holecode.blabla.setting


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivityProfileBinding
import com.holecode.blabla.pojo.UserProfile
import com.holecode.blabla.util.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var selectedImage: Uri
    private lateinit var userName: String
    private lateinit var userstatus: String
    private lateinit var dialog: AlertDialog.Builder

    // Constants
    private val PICK_IMAGE_REQUEST = 1


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

//retrieve name,status, and image from fireStore database
        SetUserFirebase.getUserInfo { user ->
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
        if (view != null) {
            when (view.id) {
                R.id.btn_done -> {
                    val name = binding.nameProfile.text.toString()
                    val status = binding.statusProfile.text.toString()
                    if (name.isEmpty()) {
                        binding.nameProfile.error = "Please type your name"
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            uploadData()
                        }
                        startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
//                        finish()
                    }
                }

                R.id.image_profile -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        OpenImageChooser()
                    }
                }
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
        val uid = SetUserFirebase.auth.currentUser?.uid.toString()
        val name = binding.nameProfile.text.toString()
        val status = binding.statusProfile.text.toString()

        val user = UserProfile(uid, name, status, "No Image")
        val reference = SetUserFirebase.currentUserStorageRef
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
    }

    private suspend fun saveUserProfile(user: UserProfile) = withContext(Dispatchers.IO) {
        SetUserFirebase.currentUserDocRef.set(user)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.progressProfile.visibility = View.VISIBLE
                /*this code compress image
                * save image in firebase realtime*/
                val uri = data.data //filePath
                val time = Date().time
                val reference = SetUserFirebase.currentUserStorageRef
                reference.putFile(uri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val filePath = uri.toString()
                            val obj = HashMap<String, Any>()
                            obj["image"] = filePath
                            SetUserFirebase.database.reference.child("users")
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

}
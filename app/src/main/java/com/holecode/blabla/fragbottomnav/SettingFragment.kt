package com.holecode.blabla.fragbottomnav

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.holecode.blabla.R
import com.holecode.blabla.databinding.FragmentSettingBinding
import com.holecode.blabla.pojo.UserProfile
import com.holecode.blabla.setting.AccountActivity
import com.holecode.blabla.setting.ProfileActivity

class SettingFragment : Fragment() {

    private lateinit var bindingFragmentSetting: FragmentSettingBinding
    private lateinit var profileActivity: ProfileActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingFragmentSetting =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return bindingFragmentSetting.root


//        profileActivity.retrieveUserData()
//        retrieveUserData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationToHome()
        //initialize firebase
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
//        retrieveUserData()


    }

    private fun navigationToHome() {
        bindingFragmentSetting.btnProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        bindingFragmentSetting.btnAccount.setOnClickListener {
            startActivity(Intent(requireContext(), AccountActivity::class.java))
        }
    }

    private fun retrieveUserData() {
        val uid = auth.uid
        val dbRef = database.reference.child("users").child(uid!!)
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
                        bindingFragmentSetting.settingName.text = name
                        bindingFragmentSetting.settingStatus.text = status
                        Glide.with(this@SettingFragment).load(image)
                            .into(bindingFragmentSetting.imageSetting)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}
package com.holecode.blabla.fragbottomnav

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.holecode.blabla.R
import com.holecode.blabla.databinding.FragmentSettingBinding
import com.holecode.blabla.setting.AccountActivity
import com.holecode.blabla.setting.ProfileActivity
import com.holecode.blabla.setting.SetUserFirebase

class SettingFragment : Fragment() {

    private lateinit var bindingFragmentSetting: FragmentSettingBinding

    private lateinit var userName: String
    private lateinit var userstatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        SetUserFirebase.getUserInfo { user ->
            userName = user.name
            userstatus = user.status
            bindingFragmentSetting.settingName.setText(userName)
            bindingFragmentSetting.settingStatus.setText(userstatus)
            if (user.imageUrl.isNotEmpty()) {
                val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.baseline_image_24) // Placeholder image resource
                    .error(R.drawable.baseline_error_24) // Error image resource

                Glide.with(this@SettingFragment)
                    .load(user.imageUrl)
                    .apply(requestOptions)
                    .into(bindingFragmentSetting.imageSetting)
            }
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationToHome()
    }

    private fun navigationToHome() {
        bindingFragmentSetting.btnProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        bindingFragmentSetting.btnAccount.setOnClickListener {
            startActivity(Intent(requireContext(), AccountActivity::class.java))
        }
    }

}
package com.holecode.blabla.fragbottomnav

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.holecode.blabla.R
import com.holecode.blabla.databinding.FragmentSettingBinding
import com.holecode.blabla.setting.AccountActivity
import com.holecode.blabla.setting.ProfileActivity

class SettingFragment : Fragment() {

    private lateinit var bindingFragmentSetting: FragmentSettingBinding
    private lateinit var profileActivity: ProfileActivity
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
       bindingFragmentSetting = DataBindingUtil.inflate(inflater,R.layout.fragment_setting, container, false)
        return bindingFragmentSetting.root

        profileActivity.retrieveUserData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationToHome()

    }
    private fun navigationToHome(){
        bindingFragmentSetting.btnProfile.setOnClickListener{
            startActivity(Intent(requireContext(),ProfileActivity::class.java))
        }
        bindingFragmentSetting.btnAccount.setOnClickListener {
            startActivity(Intent(requireContext(),AccountActivity::class.java))
        }
    }

}
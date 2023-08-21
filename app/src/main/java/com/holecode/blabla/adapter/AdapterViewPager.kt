package com.holecode.blabla.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holecode.blabla.fragbottomnav.AddFragment
import com.holecode.blabla.fragbottomnav.CallsFragment
import com.holecode.blabla.fragbottomnav.ChatsFragment
import com.holecode.blabla.fragbottomnav.SettingFragment

class AdapterViewPager(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

   private val fragmentsList = listOf(
        ChatsFragment(),
        AddFragment(),
        CallsFragment(),
        SettingFragment()
    )

    override fun getItemCount(): Int {
        // Return the total number of fragments in your adapter
        return fragmentsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
//        return when(position){
//            0 -> ChatsFragment()
//            1->AddFragment()
//            2->CallsFragment()
//            3->SettingFragment()
//            else -> throw IllegalArgumentException("Invalid position: $position")
//        }
    }
}
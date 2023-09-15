package com.holecode.blabla.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.holecode.blabla.R
import com.holecode.blabla.adapter.AdapterViewPager
import com.holecode.blabla.databinding.ActivityHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapterFragment: AdapterViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterFragment = AdapterViewPager(this)
        binding.viewPagerMain.adapter = adapterFragment
        // handle View pager about bottom navigation view
        CoroutineScope(Dispatchers.Main).launch {
            binding.viewPagerMain.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNavigationView.menu.getItem(position).isChecked = true

                }
            })

            binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.chats -> binding.viewPagerMain.currentItem = 0
                    R.id.addFriends -> binding.viewPagerMain.currentItem = 1
                    R.id.call -> binding.viewPagerMain.currentItem = 2
                    R.id.setting -> binding.viewPagerMain.currentItem = 3

                }
                true
            }
        }
    }
}
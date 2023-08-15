package com.holecode.blabla.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import com.holecode.blabla.Auth.LoginActivity
import com.holecode.blabla.R
import com.holecode.blabla.databinding.ActivitySplashScreenBinding


class SplashScreen : AppCompatActivity() {
    lateinit var  binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
//    val motionLayout :MotionLayout =findViewById(R.id.motionLayout)
    binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

        }

        override fun onTransitionChange(
            motionLayout: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float) {

        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            finish()
        }

        override fun onTransitionTrigger(
            motionLayout: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float) {

        }
    })
    }
}
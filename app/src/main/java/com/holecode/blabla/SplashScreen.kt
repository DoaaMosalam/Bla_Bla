package com.holecode.blabla

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import com.holecode.blabla.R
import com.holecode.blabla.util.LoginActivity


class SplashScreen : AppCompatActivity() {
//    lateinit var  binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash_screen)
    val motionLayout :MotionLayout =findViewById(R.id.motionLayout)
    motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
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
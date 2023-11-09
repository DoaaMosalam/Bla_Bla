package com.holecode.blabla.util

//import com.holecode.blabla.util.Utility
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.holecode.blabla.Auth.LoginActivity
import com.holecode.blabla.databinding.ActivitySplashScreenBinding


class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    companion object {
        private const val INITIAL_REQUEST = 1337
        private val INITIAL_PERMS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
        binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                startApp()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }
        })
    }

    private fun startApp() {
        startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
        finish()
    }

    private fun checkPermissions() {
        if (checkHasPermissions().not()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST)
        } else {
            startApp()
        }
    }

    private fun checkHasPermissions(): Boolean {
        var result = false
        for (i in INITIAL_PERMS) {
            result = if (isPermissionGranted(i)) true else return false
        }
        return result
    }

    private fun isPermissionGranted(i: String): Boolean {
        return ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_GRANTED
    }

//    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            INITIAL_REQUEST -> if (checkHasPermissions()) {
//                startApp()
//            } else {
//                Utility.makeText(this, "Permissions are necessary")
//                Handler().postDelayed({ checkPermissions() }, 1500)
//            }
//        }
//    }
}
package com.holecode.blabla.util


import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BasicActivity<VB:ViewBinding>:AppCompatActivity() {
    abstract val bindingInflater:(LayoutInflater) -> VB
    private lateinit var  binding:ViewBinding
    protected val bindingActivity
        get() = binding as VB?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        navigateToHomePage()
    }
    abstract fun navigateToHomePage()


}
package com.makeshaadi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val navController = findNavController(R.id.auth_nav_host_fragment)

        val s = intent.extras?.getBoolean("showLogin")
        if (s != null && s == true) {
            navController.navigate(R.id.loginFragment)
        }
    }
}
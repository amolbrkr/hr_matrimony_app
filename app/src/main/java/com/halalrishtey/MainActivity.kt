package com.halalrishtey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        bottom_navigation.setupWithNavController(findNavController(R.id.nav_host_fragment))

        val uid = applicationContext.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
            .getString("user_uid", null)

        if (uid == null) {
            val i = Intent(this, WelcomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            this.finish()
        } else {
            //TODO: Load user data using this uid.
            Toast.makeText(applicationContext, uid.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

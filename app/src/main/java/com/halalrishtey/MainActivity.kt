package com.halalrishtey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val userVM: UserViewModel by viewModels()

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
            userVM.currentUserId.value = uid
            Toast.makeText(applicationContext, uid.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

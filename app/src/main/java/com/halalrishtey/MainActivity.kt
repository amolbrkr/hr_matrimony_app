package com.halalrishtey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val userVM: UserViewModel by viewModels()
    private val userAuthVM: UserAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.overflowIcon = null

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

            userVM.getUser(uid).observe(this, Observer {
                Log.d("MainActivity", "PhotoURL: ${it.photoUrl}")
                if (!it.photoUrl.isBlank() || it.photoUrl.length > 5) {
                    Picasso.get().load(it.photoUrl)
                        .placeholder(R.drawable.ph_gray)
                        .error(R.drawable.ic_launcher_background)
                        .into(userImageView)
                } else {
                    Log.d("MainActivity", "Cannot get Photo url")
                }
            })
        }

        userImageView.setOnClickListener {
            toolbar.showOverflowMenu()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_signout -> {
                userAuthVM.signOut()
                val i = Intent(this, WelcomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                this.finish()
                return true
            }

            R.id.option_exit -> {
                return true
            }

            else -> {
                return true
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu);
        return true
    }
}

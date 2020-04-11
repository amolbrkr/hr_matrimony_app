package com.halalrishtey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.halalrishtey.viewmodels.SearchViewModel
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val userVM: UserViewModel by viewModels()
    private val userAuthVM: UserAuthViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.overflowIcon = null

        val navController = findNavController(R.id.nav_host_fragment)
        bottom_navigation.setupWithNavController(navController)

        val uid = applicationContext.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
            .getString("user_uid", null)

        if (uid == null) {
            val i = Intent(this, WelcomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            this.finish()
        } else {
            //Update registration token for FCM
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val token = it.result?.token
                        userAuthVM.updateUserData(uid, mapOf("registrationToken" to token))
                    }
                }

            userVM.currentUid.value = uid
            userVM.getUser(uid).observe(this, Observer {
                if (!it.photoUrl.isBlank() || it.photoUrl.length > 5) {
                    Picasso.get().load(it.photoUrl)
                        .placeholder(R.drawable.ph_gray)
                        .error(R.drawable.ic_launcher_background)
                        .into(userImageView)
                    userVM.getUser(uid).removeObservers(this)
                } else {
                    Log.d("MainActivity", "Cannot get Photo url")
                }
            })
        }

        userImageView.setOnClickListener {
            toolbar.showOverflowMenu()
        }

        homeSearchInp.setEndIconOnClickListener {
            SearchSheetDialog().show(supportFragmentManager, "filter_dialog")
        }

        homeSearchInp.editText?.doOnTextChanged { text, start, count, after ->
            Log.d("MainActivity", "Search Query: $text, $count, $start")

            if (navController.currentDestination?.label != "fragment_home") {
                navController.navigate(R.id.homeFragment)
            }

            if (count > 2)
                searchVM.query.value = text.toString()
        }

        toggleSearchBtn.setOnClickListener {
            if (navController.currentDestination?.label != "fragment_home") {
                navController.navigate(R.id.homeFragment)
            }

            if (homeSearchInp.visibility == View.VISIBLE)
                homeSearchInp.visibility = View.GONE
            else homeSearchInp.visibility = View.VISIBLE

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_profile -> {
                CustomUtils.openUserDetails(this, userVM.currentUser.value!!)
                return true
            }

            R.id.option_signout -> {
                userAuthVM.signOut()

                //Remove content from shared preferences
                val sharedPreferences =
                    applicationContext.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

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
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }
}

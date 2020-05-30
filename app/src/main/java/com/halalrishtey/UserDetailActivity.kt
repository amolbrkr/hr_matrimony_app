package com.halalrishtey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.DefaultImageAdapter
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity() {
    private lateinit var userImages: ArrayList<String>
    private lateinit var adapter: DefaultImageAdapter

    private val userVM by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(userDetailToolbar as Toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        userImages = ArrayList()
        adapter = DefaultImageAdapter(userImages)
        udImagesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        udImagesRV.adapter = adapter

        val userId = intent?.extras?.getString("userId")

        if (userId != null) {
            userVM.getUser(userId).observe(this, Observer { user ->
                userImages.clear()
                userImages.add(user.photoUrl)
                userImages.addAll(user.photoList)
                adapter.notifyDataSetChanged()

                if (userVM.currentUid.value == user.uid)
                    editProfileFAB.visibility = View.VISIBLE

                if (user.photoUrl.length > 10) {
                    Picasso.get().load(user.photoUrl).into(userAvatarImage)
                }

                editProfileFAB.setOnClickListener {
                    val i = Intent(this, EditProfileActivity::class.java)
                    startActivity(i)
                }

                profileOptionsFAB.setOnClickListener {
                    ProfileOptionsSheet(user).show(supportFragmentManager, "profile_options")
                }

                if (user.isIdProofVerified) userVerfiedBadge.visibility = View.VISIBLE
                userNameText.text = user.displayName
                userSubText.text = "${user.gender}, ${user.age} Years"
                detail1Text.text =
                    "Works at: ${user.workLocation} \nEducation: ${user.qualification} \nOrganization: ${user.organizationName}"
                detail2Text.text =
                    "Income: ${user.annualIncome} per anum \nMaritial Status: ${user.maritalStatus} \nSect: ${user.sect}"
            })
        }
    }
}

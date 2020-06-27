package com.makeshaadi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeshaadi.adapter.DefaultImageAdapter
import com.makeshaadi.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity() {
    private lateinit var userImages: ArrayList<String>
    private lateinit var adapter: DefaultImageAdapter

    private val userVM by viewModels<UserViewModel>()

    @SuppressLint("SetTextI18n")
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

                if (userVM.currentUid.value == user.uid) {
                    editProfileFAB.visibility = View.VISIBLE
                    profileOptionsFAB.visibility = View.GONE
                }

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
                bioText.text = ifValid(user.bio)

                detail1Text.text =
                    "Height: ${ifValid(user.height)}\n" +
                            "Qualification: ${ifValid(user.qualification)}\n" +
                            "Details: ${ifValid(user.qualDetails)}\n" +
                            "Profession: ${ifValid(user.organizationName)}\n" +
                            "Work Location: ${ifValid(user.workLocation)}\n" +
                            "Annual Income: ${ifValid(user.annualIncome)}\n"

                detail2Text.text = "Father's Name: ${ifValid(user.fathersName)}\n" +
                        "Father's Job: ${ifValid(user.fathersJob)}\n" +
                        "Number of Brothers: ${ifValid(user.numBrothers)}\n" +
                        "Number of Sisters: ${ifValid(user.numSisters)}\n"

                detail3Text.text = "Marital Status: ${ifValid(user.maritalStatus)}\n" +
                        "Dargah: ${ifValid(user.dargah)}\n" +
                        "Sect: ${ifValid(user.sect)}\n" +
                        "Joined On: ${CustomUtils.genDateString(user.createdAt)}"
            })
        }
    }

    fun showMsg(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    fun ifValid(str: String): String {
        return if (str.isNotBlank() && str.length > 0)
            str; else "No info provided."
    }
}

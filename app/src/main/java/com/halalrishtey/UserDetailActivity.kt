package com.halalrishtey

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.halalrishtey.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(userDetailToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val user = intent?.extras?.get("userData") as User
        Log.d("UserDetail", "Received user data of ${user.displayName} from MainActivity.")

        if (user.photoUrl.length > 10) {
            Picasso.get().load(user.photoUrl).into(userMainImage)
            Picasso.get().load(user.photoUrl).into(userAvatarImage)
        }

        if (user.isIdProofVerified) userVerfiedBadge.visibility = View.VISIBLE
        userNameText.text = user.displayName
        userSubText.text = "${user.gender}, ${user.age} Years"
        detail1Text.text =
            "Works at: ${user.workLocation} \nEducation: ${user.education} \nOrganization: ${user.organizationName}"
        detail2Text.text =
            "Income: ${user.annualIncome} per anum \nMaritial Status: ${user.maritalStatus} \nSect: ${user.sect}"
    }
}

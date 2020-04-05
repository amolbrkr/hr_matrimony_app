package com.halalrishtey

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity() {
    private val userVM by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(userDetailToolbar as Toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val user = intent?.extras?.get("userData") as User
        Log.d("UserDetail", "Received user data of ${user.displayName} from MainActivity.")

        if (userVM.currentUid.value == user.uid) {
            shareProfileFAB.visibility = View.VISIBLE
            editProfileFAB.visibility = View.VISIBLE
        } else {
            shareProfileFAB.visibility = View.GONE
            editProfileFAB.visibility = View.GONE
        }


        if (user.photoUrl.length > 10) {
            Picasso.get().load(user.photoUrl).into(userMainImage)
            Picasso.get().load(user.photoUrl).into(userAvatarImage)
        }


        editProfileFAB.setOnClickListener {
            val i = Intent(this, EditProfileActivity::class.java)
            startActivity(i)
        }

        shareProfileFAB.setOnClickListener {
            val i = Intent().apply {
                action = Intent.ACTION_SEND
                if (user.photoUrl.length > 10) {
                    val d = userMainImage.drawable as BitmapDrawable
                    val bmp = d.bitmap
                    val path = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        bmp,
                        user.displayName + "_main",
                        null
                    )
                    val uri = Uri.parse(path)
                    putExtra(Intent.EXTRA_STREAM, uri)
                }

                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out ${user.displayName}'s profile on Halal Rishtey App!\n\n Get the app now from #"
                )
                type = "*/*"
            }
            startActivity(i)
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

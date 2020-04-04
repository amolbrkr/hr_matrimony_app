package com.halalrishtey

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.halalrishtey.adapter.SpinnerAdapters
import com.halalrishtey.models.ProfilePicVisibility
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val userVM by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(editToolbar as Toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Profile"

        epProfilePicFAB.setOnClickListener {
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val adapters = SpinnerAdapters(this)

        Log.d("EditProfile", "Current user: ${userVM.currentUser.value}")
        userVM.currentUser.observe(this, Observer {
            if (it.photoUrl.length > 10) Picasso.get().load(it.photoUrl).into(avatarImage)

            epNameText.text = it.displayName
            epSubText.text = "${it.gender}, ${it.age} Yrs, ${it.phoneNumber}"
            epUidText.text = it.uid
            epBioTextInp.editText?.setText(it.bio)

            when (it.profilePicVisibility) {
                ProfilePicVisibility.Public -> profileVisRadios.check(R.id.publicSelector)
                ProfilePicVisibility.Interests -> profileVisRadios.check(R.id.interestSelector)
                else -> profileVisRadios.check(R.id.onlyMeSelector)
            }

            epHeightSpinner.adapter = adapters.heightAdapter
            epHeightSpinner.setSelection(adapters.heightAdapter.getPosition(it.height))

            epMaritalStatusSpinner.adapter = adapters.maritalStatusAdapter
            epMaritalStatusSpinner.setSelection(adapters.maritalStatusAdapter.getPosition(it.maritalStatus))

            epEduSpinner.adapter = adapters.highestEducationAdapter
            epEduSpinner.setSelection(adapters.highestEducationAdapter.getPosition(it.education))

            epWorkLocText.editText?.setText(it.workLocation)
            epOrgNameText.editText?.setText(it.organizationName)
            epIncomeText.editText?.setText(it.annualIncome.toString())
            epSiblingText.editText?.setText(it.numberOfSiblings)
            fOccText.editText?.setText(it.fathersJob)
            mOccText.editText?.setText(it.mothersJob)

            epSectSpinner.adapter = adapters.sectAdapter
            epSectSpinner.setSelection(adapters.sectAdapter.getPosition(it.sect))

            epDargahSpinner.adapter = adapters.dargahAdapter
            epDargahSpinner.setSelection(adapters.dargahAdapter.getPosition(it.dargah))
        })
    }
}

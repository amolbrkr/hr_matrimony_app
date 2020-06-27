package com.makeshaadi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.makeshaadi.adapter.ProfileImagesAdapter
import com.makeshaadi.adapter.SpinnerAdapters
import com.makeshaadi.adapter.UnblockAdapter
import com.makeshaadi.models.ProfilePicVisibility
import com.makeshaadi.models.User
import com.makeshaadi.services.StorageService
import com.makeshaadi.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ProfileImagesAdapter
    private lateinit var unblockAdapter: UnblockAdapter
    private lateinit var userImages: ArrayList<String>
    private lateinit var blockedUsers: ArrayList<User>
    private val userVM by viewModels<UserViewModel>()
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(editToolbar as Toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Edit Profile"

        userImages = ArrayList()
        blockedUsers = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fun removeImageFromDB(imageUrl: String) {
            userVM.updateUserData(
                userVM.currentUid.value!!,
                mapOf("photoList" to FieldValue.arrayRemove(imageUrl))
            ).observe(this, Observer {
                Toast.makeText(this, "Picture Deleted!", Toast.LENGTH_SHORT).show()
            })
        }

        adapter = ProfileImagesAdapter(userImages, ::removeImageFromDB)
        unblockAdapter = UnblockAdapter(blockedUsers) { targetId ->
            val currentId = userVM.currentUid.value!!
            userVM.unblockUser(currentId, targetId).observe(this, Observer {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            })
        }

        blockedRV.layoutManager = LinearLayoutManager(this)
        blockedRV.adapter = unblockAdapter

        profileImgRV.layoutManager = layoutManager
        profileImgRV.adapter = adapter

        addNewImgBtn.setOnClickListener {
            if (userVM.currentUser.value!!.photoList.size < 8) {
                val i = Intent()
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(i, 1)
            } else Snackbar.make(it, "You can only upload up to 7 images.", Snackbar.LENGTH_SHORT)
                .show()
        }

        epProfilePicFAB.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, 2)
        }

        epSaveBtn.setOnClickListener {
            if (validateData() == null) {
                val temp = userVM.currentUser.value

                userData.apply {
                    bio = epBioTextInp.editText?.text.toString()
                    profilePicVisibility = when {
                        publicSelector.isChecked -> ProfilePicVisibility.Public
                        interestSelector.isChecked -> ProfilePicVisibility.Interests
                        else -> ProfilePicVisibility.OnlyMe
                    }
                    height = epHeightSpinner.selectedItem.toString()
                    maritalStatus = epMaritalStatusSpinner.selectedItem.toString()
                    qualification = epEduSpinner.selectedItem.toString()
                    workLocation = epWorkLocText.editText?.text.toString()
                    organizationName = epOrgNameText.editText?.text.toString()
                    annualIncome = epIncomeSpinner.selectedItem.toString()
                    fathersName = fNameText.editText?.text.toString()
                    fathersJob = fOccText.editText?.text.toString()
                    sect = epSectSpinner.selectedItem.toString()
                    dargah = epDargahSpinner.selectedItem.toString()
                    qualDetails = epQualInp.editText?.text.toString()
                    numBrothers = epNumBroText.editText?.text.toString()
                    numSisters = epNumSisText.editText?.text.toString()
                }

                userVM.updateUserData(temp?.uid!!, temp).observe(this, Observer { res ->
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                    finish()
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val adapters = SpinnerAdapters(this)

        userVM.currentUser.observe(this, Observer {
            userData = it
            //Update & show blocked profiles
            if (it.blockList.size > 0) {
                userVM.getProfilesByIds(it.blockList).observe(this, Observer { userList ->
                    blockedUsers.clear()
                    blockedUsers.addAll(userList)
                    unblockAdapter.notifyDataSetChanged()
                })
            }
            //Update recyclerview to show user uploaded images
            userImages.clear()
            userImages.addAll(it.photoList)
            adapter.notifyDataSetChanged()

            if (it.photoUrl.length > 10) Picasso.get().load(it.photoUrl).into(avatarImage)

            epNameText.text = it.displayName
            epSubText.text = "${it.gender}, ${it.age} Yrs, ${it.phoneNumber}"
            epUidText.text = it.email
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
            epEduSpinner.setSelection(adapters.highestEducationAdapter.getPosition(it.qualification))

            epWorkLocText.editText?.setText(it.workLocation)
            epOrgNameText.editText?.setText(it.organizationName)

            epIncomeSpinner.adapter = adapters.incomeAdapter
            epIncomeSpinner.setSelection((adapters.highestEducationAdapter.getPosition(it.annualIncome)))

            fNameText.editText?.setText(it.fathersName)
            fOccText.editText?.setText(it.fathersJob)

            epSectSpinner.adapter = adapters.sectAdapter
            epSectSpinner.setSelection(adapters.sectAdapter.getPosition(it.sect))

            epDargahSpinner.adapter = adapters.dargahAdapter
            epDargahSpinner.setSelection(adapters.dargahAdapter.getPosition(it.dargah))
        })
    }

    private fun validateData(): String? {
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            StorageService.uploadImgToStorage(data.data!!).observe(this, Observer {
                if (it.errorMsg != null)
                    Toast.makeText(this, it.errorMsg, Toast.LENGTH_SHORT).show()
                else {
                    userVM.updateUserData(
                        userVM.currentUid.value!!,
                        mapOf("photoUrl" to it.fileUrl!!)
                    ).observe(this, Observer { s ->
                        Log.d("EditProfile", s)
                    })

                    Picasso.get().load(it.fileUrl!!).into(avatarImage)
                }
            })
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            StorageService.uploadImgToStorage(data.data!!).observe(this, Observer {
                if (it.errorMsg != null)
                    Toast.makeText(this, it.errorMsg, Toast.LENGTH_SHORT).show()
                else {
                    userVM.updateUserData(
                        userVM.currentUid.value!!,
                        mapOf("photoList" to FieldValue.arrayUnion(it.fileUrl!!))
                    ).observe(this, Observer { s ->
                        Log.d("EditProfile", s)
                    })

                    userImages.add(it.fileUrl!!)
                    adapter.notifyDataSetChanged()
                }
            })
        } else {
            Toast.makeText(
                this,
                "Something went wrong! Please Try Again Later!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

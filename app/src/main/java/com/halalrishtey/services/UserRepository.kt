package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.halalrishtey.models.Gender
import com.halalrishtey.models.User

object UserRepository {
    fun getAllUserProfiles(): MutableLiveData<ArrayList<User>> {
        val listOfUsers = MutableLiveData<ArrayList<User>>()
        DatabaseRepository
            .getDbInstance()
            .collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tempList = ArrayList<User>()

                    task.result?.documents?.forEach {
                        //Skipped date of birth below
                        tempList.add(
                            User(
                                email = it.get("email") as String,
                                uid = it.get("uid") as String,
                                displayName = it.get("displayName") as String,
                                age = it.get("age").toString().toInt(),
                                photoUrl = it.get("photoUrl") as String,
                                idProofUrl = it.get("idProofUrl") as String,
                                phoneNumber = it.get("phoneNumber") as Number,
                                gender = Gender.valueOf(it.get("gender") as String),
                                createdFor = it.get("createdFor") as String,
                                lastSignInAt = System.currentTimeMillis(),
                                address = it.get("address")?.toString() ?: "Not found",
                                height = it.get("height") as String,
                                education = it.get("education") as String,
                                workLocation = it.get("workLocation") as String,
                                sect = it.get("sect") as String,
                                dargah = it.get("dargah") as String,
                                maritalStatus = it.get("maritalStatus") as String,
                                locationLat = it.get("locationLat")?.toString()?.toDouble() ?: 0.0,
                                locationLong = it.get("locationLong")?.toString()?.toDouble()
                                    ?: 0.0,
                                countryCode = it.get("countryCode") as String,
                                pincode = it.get("pincode")?.toString() ?: "Not found",
                                isOTPVerified = it.get("otpverified") as Boolean,
                                countryCallingCode = it.get("countryCallingCode") as String
                                //interestCount = it.get("interestCount") as Int
                            )
                        )
                    }
                    Log.d("UserRepo", tempList.toString())
                    listOfUsers.value = tempList
//                    Log.d("UserRepository", task.result?.documents[0].)
                } else {
                    Log.d("UserRepository", "Error: ${task.exception?.message}")
                }
            }
        return listOfUsers
    }
}
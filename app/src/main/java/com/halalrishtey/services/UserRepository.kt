package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.halalrishtey.models.Gender
import com.halalrishtey.models.User

object UserRepository {
    fun getProfileOfUser(userId: String): MutableLiveData<User> {
        val fetchedUser = MutableLiveData<User>()

        DatabaseRepository
            .getDbInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchedUser.value = User(
                        email = task.result?.data?.get("email") as String,
                        uid = task.result?.data?.get("uid") as String,
                        displayName = task.result?.data?.get("displayName") as String,
                        age = task.result?.data?.get("age").toString().toInt(),
                        photoUrl = task.result?.data?.get("photoUrl") as String,
                        idProofUrl = task.result?.data?.get("idProofUrl") as String,
                        phoneNumber = task.result?.data?.get("phoneNumber") as Number,
                        gender = Gender.valueOf(task.result?.data?.get("gender") as String),
                        createdFor = task.result?.data?.get("createdFor") as String,
                        lastSignInAt = System.currentTimeMillis(),
                        address = task.result?.data?.get("address")?.toString() ?: "Not found",
                        height = task.result?.data?.get("height") as String,
                        education = task.result?.data?.get("education") as String,
                        workLocation = task.result?.data?.get("workLocation") as String,
                        sect = task.result?.data?.get("sect") as String,
                        dargah = task.result?.data?.get("dargah") as String,
                        maritalStatus = task.result?.data?.get("maritalStatus") as String,
                        locationLat = task.result?.data?.get("locationLat")?.toString()?.toDouble()
                            ?: 0.0,
                        locationLong = task.result?.data?.get("locationLong")?.toString()?.toDouble()
                            ?: 0.0,
                        countryCode = task.result?.data?.get("countryCode") as String,
                        pincode = task.result?.data?.get("pincode")?.toString() ?: "Not found",
                        isOTPVerified = task.result?.data?.get("otpverified") as Boolean,
                        countryCallingCode = task.result?.data?.get("countryCallingCode") as String
                        //interestCount = it.get("interestCount") as Int
                    )
                } else {
                    fetchedUser.value = null
                    Log.d("UserRepository", "")
                }
            }
        return fetchedUser
    }

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
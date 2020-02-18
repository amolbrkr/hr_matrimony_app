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
                        email = task.result?.data?.get("email").toString(),
                        uid = task.result?.data?.get("uid").toString(),
                        displayName = task.result?.data?.get("displayName").toString(),
                        age = task.result?.data?.get("age").toString().toInt(),
                        photoUrl = task.result?.data?.get("photoUrl").toString(),
                        idProofUrl = task.result?.data?.get("idProofUrl").toString(),
                        phoneNumber = task.result?.data?.get("phoneNumber") as Number,
                        gender = Gender.valueOf(task.result?.data?.get("gender").toString()),
                        createdFor = task.result?.data?.get("createdFor").toString(),
                        lastSignInAt = System.currentTimeMillis(),
                        address = task.result?.data?.get("address")?.toString() ?: "Not found",
                        height = task.result?.data?.get("height").toString(),
                        education = task.result?.data?.get("education").toString(),
                        workLocation = task.result?.data?.get("workLocation").toString(),
                        sect = task.result?.data?.get("sect").toString(),
                        dargah = task.result?.data?.get("dargah").toString(),
                        maritalStatus = task.result?.data?.get("maritalStatus").toString(),
                        locationLat = task.result?.data?.get("locationLat")?.toString()?.toDouble()
                            ?: 0.0,
                        locationLong = task.result?.data?.get("locationLong")?.toString()?.toDouble()
                            ?: 0.0,
                        countryCode = task.result?.data?.get("countryCode").toString(),
                        pincode = task.result?.data?.get("pincode")?.toString() ?: "Not found",
                        isOTPVerified = task.result?.data?.get("otpverified") as Boolean,
                        countryCallingCode = task.result?.data?.get("countryCallingCode").toString()
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
                                email = it.get("email").toString(),
                                uid = it.get("uid").toString(),
                                displayName = it.get("displayName").toString(),
                                age = it.get("age").toString().toInt(),
                                photoUrl = it.get("photoUrl").toString(),
                                idProofUrl = it.get("idProofUrl").toString(),
                                phoneNumber = it.get("phoneNumber") as Number,
                                gender = Gender.valueOf(it.get("gender").toString()),
                                createdFor = it.get("createdFor").toString(),
                                lastSignInAt = System.currentTimeMillis(),
                                address = it.get("address")?.toString() ?: "Not found",
                                height = it.get("height").toString(),
                                education = it.get("education").toString(),
                                workLocation = it.get("workLocation").toString(),
                                sect = it.get("sect").toString(),
                                dargah = it.get("dargah").toString(),
                                maritalStatus = it.get("maritalStatus").toString(),
                                locationLat = it.get("locationLat")?.toString()?.toDouble() ?: 0.0,
                                locationLong = it.get("locationLong")?.toString()?.toDouble()
                                    ?: 0.0,
                                countryCode = it.get("countryCode").toString(),
                                pincode = it.get("pincode")?.toString() ?: "Not found",
                                isOTPVerified = it.get("otpverified") as Boolean,
                                countryCallingCode = it.get("countryCallingCode").toString()
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
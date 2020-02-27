package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.halalrishtey.CustomUtils
import com.halalrishtey.models.Gender
import com.halalrishtey.models.User

object UserRepository {
    fun initInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        val interest = mapOf(currentUserId to true, targetUserId to false);

        val ref = DatabaseRepository.getDbInstance()
            .collection("interests")
            .document()

        ref.set(interest).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(
                    "UserRepo",
                    "Successfully created interest between $currentUserId & $targetUserId"
                )

                val currentRef = DatabaseRepository.getDbInstance()
                    .collection("users")
                    .document(currentUserId)

                val targetRef = DatabaseRepository.getDbInstance()
                    .collection("users")
                    .document(targetUserId)

                DatabaseRepository.getDbInstance().runBatch { batch ->
                    batch.update(currentRef, "interestsList", FieldValue.arrayUnion(ref.id))
                    batch.update(targetRef, "interestsList", FieldValue.arrayUnion(ref.id))
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        res.value = "Successfully showed your interest!"
                    } else {
                        res.value = "Oops! Something went wrong!"
                    }
                }
            } else {
                res.value = "Cannot create new interest";
            }
        }

        return res;
    }

    fun shortlistUser(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val ref = DatabaseRepository
            .getDbInstance()
            .collection("users")
            .document(currentUserId)

        ref.update("shortlistedProfiles", FieldValue.arrayUnion(targetUserId))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "UserRepo",
                        "Succesfully shorlisted target: $targetUserId for $currentUserId"
                    )
                    result.value = "Successfully Shortlisted"
                } else result.value = it.exception?.message
            }

        return result;
    }

    fun getProfileOfUser(userId: String): MutableLiveData<User> {
        val fetchedUser = MutableLiveData<User>()

        DatabaseRepository
            .getDbInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchedUser.value = CustomUtils.convertToUser(task.result!!)
                } else {
                    fetchedUser.value = null
                    Log.d("UserRepository", "Couldn't get profile of $userId")
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
                        tempList.add(CustomUtils.convertToUser(it))
                    }
                    listOfUsers.value = tempList
                } else {
                    Log.d("UserRepository", "Error: ${task.exception?.message}")
                }
            }
        return listOfUsers
    }
}
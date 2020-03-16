package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.halalrishtey.CustomUtils
import com.halalrishtey.models.User

object UserRepository {
    fun initInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val ref = DatabaseRepository
            .getDbInstance()
            .collection("users")
            .document(currentUserId)

        ref.update("interestedProfiles", FieldValue.arrayUnion(targetUserId))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "UserRepo",
                        "Succesfully expressed interest to target: $targetUserId for $currentUserId"
                    )
                    result.value = "We'll let them know that you're interested!"
                } else result.value = it.exception?.message
            }

        return result
    }

    fun removeInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()

        val ref = DatabaseRepository
            .getDbInstance()
            .collection("users")
            .document(currentUserId)

        ref.update("interestedProfiles", FieldValue.arrayRemove(targetUserId))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "UserRepo",
                        "Succesfully removed interest target: $targetUserId for $currentUserId"
                    )
                    res.value = "You're no longer interested in this user, got it!"
                } else res.value = it.exception?.message
            }

        return res
    }

    fun initConversation(currentUserId: String, targetUser: User): MutableLiveData<Boolean> {
        val r = MutableLiveData(false)

        val ts = System.currentTimeMillis()

        val convoRef = DatabaseRepository.getDbInstance()
            .collection("conversations")
            .document()

        val convoObj: Map<String, java.io.Serializable> = mapOf(
            "initialTimestamp" to ts,
            "conversationId" to convoRef.id,
            "lastMessage" to "",
            "displayName" to targetUser.displayName,
            "photoUrl" to targetUser.photoUrl
        )

        val ref1 = DatabaseRepository.getDbInstance()
            .collection("users")
            .document(currentUserId)

        val ref2 = DatabaseRepository.getDbInstance()
            .collection("users")
            .document(targetUser.uid!!)

        DatabaseRepository.getDbInstance()
            .runBatch {
                it.update(ref1, "conversations", FieldValue.arrayUnion(convoObj))
                it.update(ref2, "conversations", FieldValue.arrayUnion(convoObj))
                it.set(convoRef, mapOf("initialTimestamp" to ts))
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    r.value = true
                    Log.d(
                        "UserRepository",
                        "Successfully initialized conversation with ${targetUser.displayName}"
                    )
                } else {
                    r.value = false
                    Log.d(
                        "UserRepository",
                        "Failed to initialize conversation with ${targetUser.displayName}, Error: ${task.exception?.message}"
                    )
                }
            }
        return r
    }

    fun sendMessage(conversationId: String, message: String) {
        val ts = System.currentTimeMillis()

        DatabaseRepository.getDbInstance()
            .collection("conversations")
            .document(conversationId)
            .update(
                "messages",
                FieldValue.arrayUnion(
                    mapOf(
                        "text" to message,
                        "timestamp" to ts,
                        "readStatus" to false
                    )
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "UserRepository",
                        "Successfully sent message! conversationId: $conversationId"
                    )
                } else {
                    Log.d(
                        "UserRepository",
                        "Failed to send message to $conversationId, error: ${it.exception?.message}"
                    )
                }
            }
    }

    fun getProfilesByIds(listOfIds: ArrayList<String>): MutableLiveData<ArrayList<User>> {
        val res = MutableLiveData<ArrayList<User>>()
        val temp = ArrayList<User>()

        listOfIds.forEach { uid ->
            DatabaseRepository.getDbInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        temp.add(CustomUtils.convertToUser(it.result!!))
                        res.value = temp
                    } else {
                        Log.d(
                            "UserRepository",
                            "Cannot get user data for id $uid, err: ${it.exception?.message}"
                        )
                    }
                }
        }

        return res
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
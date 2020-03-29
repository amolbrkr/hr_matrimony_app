package com.halalrishtey.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.halalrishtey.models.User
import com.halalrishtey.services.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val currentUid = MutableLiveData<String>()
    var currentUser = MutableLiveData<User>()

    init {
        val uid = application.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
            .getString("user_uid", null)

        if (uid != null) {
            currentUid.value = uid
            Log.d("UserViewModel", "currentUid set to: ${currentUid.value}")
            currentUser = observeUser(uid).also {
                Log.d(
                    "UserViewModel",
                    "Current user set to: ${it.value?.displayName}"
                )
            }
        }
    }

    fun getUser(uid: String): MutableLiveData<User> {
        return if (uid == currentUid.value) {
            if (currentUser.value == null) {
                Log.d("UserViewModel", "Made repo call to get user!")
                currentUser = UserRepository.getProfileOfUser(uid)
                currentUser
            } else currentUser
        } else UserRepository.getProfileOfUser(uid)
    }

    fun observeUser(userId: String) = UserRepository.observeUserProfile(userId)

    fun getProfilesByIds(listOfIds: ArrayList<String>) = UserRepository.getProfilesByIds(listOfIds)

    fun initInterest(
        currentUserId: String,
        currentUserName: String,
        targetUserId: String
    ): MutableLiveData<String> {
        val t = currentUser.value!!
        t.interestedProfiles.add(targetUserId)
        currentUser.value = t
        return UserRepository.initInterest(currentUserId, currentUserName, targetUserId)
    }

    fun removeInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val t = currentUser.value!!
        t.interestedProfiles.remove(targetUserId)
        currentUser.value = t
        return UserRepository.removeInterest(currentUserId, targetUserId)
    }

    fun initConversation(currentUser: User, targetUser: User) =
        UserRepository.initConversation(currentUser, targetUser)

    fun getConversationsByIds(listOfIds: ArrayList<String>) =
        UserRepository.getConversationsByIds(listOfIds)

    fun observeConversation(conversationId: String) =
        UserRepository.observeConversation(conversationId)

    fun sendMessage(conversationId: String, senderId: String, text: String) =
        UserRepository.sendMessage(conversationId, senderId, text)

    fun blockUser(currentId: String, targetId: String) =
        UserRepository.blockUser(currentId, targetId)

    fun reportUser(current: User, target: User, reason: String) =
        UserRepository.reportUser(current, target, reason)
}

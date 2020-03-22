package com.halalrishtey.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halalrishtey.models.User
import com.halalrishtey.services.UserRepository

class UserViewModel : ViewModel() {
    val currentUserId = MutableLiveData<String>()
    var currentUserProfile = MutableLiveData<User>()

    fun getUser(uid: String): MutableLiveData<User> {
        return if (uid == currentUserId.value) {
            if (currentUserProfile.value == null) {
                Log.d("UserViewModel", "Made repo call to get user!")
                currentUserProfile = UserRepository.getProfileOfUser(uid)
                currentUserProfile
            } else currentUserProfile
        } else UserRepository.getProfileOfUser(uid)
    }

    fun getProfilesByIds(listOfIds: ArrayList<String>) = UserRepository.getProfilesByIds(listOfIds)

    fun initInterest(
        currentUserId: String,
        currentUserName: String,
        targetUserId: String
    ): MutableLiveData<String> {
        val t = currentUserProfile.value!!
        t.interestedProfiles.add(targetUserId)
        currentUserProfile.value = t
        return UserRepository.initInterest(currentUserId, currentUserName, targetUserId)
    }

    fun removeInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val t = currentUserProfile.value!!
        t.interestedProfiles.remove(targetUserId)
        currentUserProfile.value = t
        return UserRepository.removeInterest(currentUserId, targetUserId)
    }

    fun initConversation(currentUser: User, targetUser: User) =
        UserRepository.initConversation(currentUser, targetUser)

    fun sendMessage(conversationId: String, senderId: String, text: String) =
        UserRepository.sendMessage(conversationId, senderId, text)
}

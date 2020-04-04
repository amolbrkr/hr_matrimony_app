package com.halalrishtey.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.halalrishtey.CustomUtils
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
            currentUser = observeUser(uid)
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

    fun initConversation(current: User, target: User): MutableLiveData<String> {
        val t = currentUser.value!!
        t.conversations.add(CustomUtils.genCombinedHash(current.uid!!, target.uid!!))
        currentUser.value = t
        return UserRepository.initConversation(current, target)
    }

    fun getConversationsByIds(listOfIds: ArrayList<String>) =
        UserRepository.getConversationsByIds(listOfIds)

    fun observeConversation(conversationId: String) =
        UserRepository.observeConversation(conversationId)

    fun updateReadStatus(convoId: String, currentUserId: String) =
        UserRepository.updateReadStatus(convoId, currentUserId)

    fun sendMessage(conversationId: String, senderId: String, recieverId: String, text: String) =
        UserRepository.sendMessage(conversationId, senderId, recieverId, text)

    fun blockUser(currentId: String, targetId: String): MutableLiveData<String> {
        val t = currentUser.value!!
        t.blockList.add(targetId)
        currentUser.value = t
        return UserRepository.blockUser(currentId, targetId)
    }

    fun reportUser(current: String, target: String, reason: String) =
        UserRepository.reportUser(current, target, reason)
}

package com.halalrishtey.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.UserRecoverableException
import com.halalrishtey.models.User
import com.halalrishtey.services.UserRepository

class UserViewModel : ViewModel() {
    val currentUserId = MutableLiveData<String>()
    fun getUser(uid: String) = UserRepository.getProfileOfUser(uid)
    fun shortlistUser(currentUserId: String, targetUserId: String) =
        UserRepository.shortlistUser(currentUserId, targetUserId)
}
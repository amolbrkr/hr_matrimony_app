package com.halalrishtey.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halalrishtey.models.User
import com.halalrishtey.services.UserRepository

class UserViewModel : ViewModel() {
    val currentUserId = MutableLiveData<String>()
    fun getUser(uid: String) = UserRepository.getProfileOfUser(uid)
}
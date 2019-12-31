package com.halalrishtey.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.halalrishtey.services.AuthService


class UserAuthViewModel : ViewModel() {
    private val authService = AuthService()
    var authenticatedUserLiveData: LiveData<FirebaseUser>? = null
    var currentUserLiveData: MutableLiveData<FirebaseUser>? = null

    init {
        currentUserLiveData = authService.getCurrentUser()
    }

    fun signInWithEmail(email: String, password: String) {
        authenticatedUserLiveData = authService.loginWithEmail(email, password)
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        authenticatedUserLiveData = authService.createNewUser(email, password)
    }
}
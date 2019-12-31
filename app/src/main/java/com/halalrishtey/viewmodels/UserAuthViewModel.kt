package com.halalrishtey.viewmodels

import androidx.lifecycle.ViewModel
import com.halalrishtey.services.AuthRepository


class UserAuthViewModel : ViewModel() {
    private val authService = AuthRepository()

    fun signIn(email: String, password: String) = authService.loginWithEmail(email, password)

    fun signUp(email: String, password: String) = authService.createNewUser(email, password)
}
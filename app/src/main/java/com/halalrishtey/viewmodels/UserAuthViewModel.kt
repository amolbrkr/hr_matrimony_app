package com.halalrishtey.viewmodels

import androidx.lifecycle.ViewModel
import com.halalrishtey.services.AuthRepository


class UserAuthViewModel : ViewModel() {
    private val authService = AuthRepository()

    fun validateEmail(email: String): String? {
        return if (email.isEmpty() || email.isBlank()) {
            "Email field can't be empty"
        } else if (email.length < 8) {
            "Email is too short"
        } else if (!email.contains('@') && !email.contains('.')) {
            "Email seems to be invalid"
        } else if (email.contains(" ")) {
            "Email don't contain spaces"
        } else null
    }

    fun validatePassword(pw: String): String? {
        return if (pw.isEmpty() || pw.isBlank()) {
            "Password can't be empty"
        } else if (pw.length < 8) {
            "Password is too short"
        } else null
    }

    fun signIn(email: String, password: String) = authService.loginWithEmail(email, password)

    fun signUp(email: String, password: String) = authService.createNewUser(email, password)

    fun signOut() = authService.logOut()

    fun sendResetPasswordEmail(email: String) = authService.resetPassword(email)
}
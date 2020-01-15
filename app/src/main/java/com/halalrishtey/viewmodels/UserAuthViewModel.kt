package com.halalrishtey.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.halalrishtey.models.User
import com.halalrishtey.services.AuthRepository


class UserAuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authService = AuthRepository()

    val newUser = MutableLiveData<User>()
    val pwd = MutableLiveData<String>()
    val locationUpdates = LocationLiveData(application)

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

    fun validatePhone(phone: String): String? {
        return if (phone.isEmpty()) {
            "Phone number can't be empty"
        } else if (phone.length < 9) {
            "Phone number seems too short"
        } else null
    }

    fun signIn(email: String, password: String) = authService.loginWithEmail(email, password)

    fun signUp(email: String, password: String, userData: User) =
        authService.createNewUser(email, password, userData)

    fun signOut() = authService.logOut()

    fun sendResetPasswordEmail(email: String) = authService.resetPassword(email)
}
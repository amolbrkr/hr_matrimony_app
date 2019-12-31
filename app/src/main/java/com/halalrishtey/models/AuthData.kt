package com.halalrishtey.models

import com.google.firebase.auth.FirebaseUser

data class AuthData(
    val data: FirebaseUser?,
    val errorMessage: String?
)
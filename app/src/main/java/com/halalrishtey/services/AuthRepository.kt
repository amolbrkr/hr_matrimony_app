package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.halalrishtey.models.AuthData
import com.halalrishtey.models.User

class AuthRepository {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun currentUser() = firebaseAuth.currentUser

    fun createNewUser(email: String, password: String): MutableLiveData<AuthData> {
        val authenticatedUserLiveData = MutableLiveData<AuthData>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Created new user! ${task.result?.user?.email}")
                    authenticatedUserLiveData.value = AuthData(
                        User(
                            task.result?.user!!
                        ), null
                    )
                } else {
                    Log.d(
                        "AuthService",
                        "Failed to create new user! $email ${task.exception?.message}"
                    )
                    authenticatedUserLiveData.value =
                        AuthData(null, task.exception?.message)
                }
            }

        return authenticatedUserLiveData
    }

    fun addNewUserToDB(uid: String, userData: User): MutableLiveData<String?> {
        val msg = MutableLiveData<String?>()
        DatabaseRepository.getDbInstance().collection("users")
            .document(uid).set(userData).addOnCompleteListener {
                Log.d("AuthService", "Added new user to database. ${userData.email}")
                if (it.isSuccessful) {
                    msg.value = "Successfully created!"
                }
            }
        return msg
    }

    fun loginWithEmail(email: String, password: String): MutableLiveData<AuthData> {
        val userLiveData = MutableLiveData<AuthData>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Sign in with new user! $email")
                    userLiveData.value =
                        AuthData(User(task.result?.user!!), null)
                } else {
                    Log.d(
                        "AuthService",
                        "Failed to sign in! $email ${task.exception?.message}"
                    )
                    userLiveData.value = AuthData(null, task.exception?.message)
                }
            }

        return userLiveData
    }

    fun sendVerificationEmail(): MutableLiveData<String?> {
        val msg = MutableLiveData<String?>()

        if (currentUser() != null) {
            currentUser()?.sendEmailVerification()
                ?.addOnCompleteListener { task: Task<Void> ->
                    if (!task.isSuccessful) {
                        msg.value = task.exception?.message
                    }
                }
        } else {
            msg.value = "User is not logged in!"
        }

        return msg
    }

    fun logOut() {
        firebaseAuth.signOut()
    }

    fun resetPassword(email: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                res.value = if (it.isSuccessful) {
                    "Check your email to reset your password."
                } else {
                    "There was some error while sending you the reset password mail."
                }
            }
        return res
    }
}
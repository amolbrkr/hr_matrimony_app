package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.halalrishtey.models.AuthData

class AuthRepository {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun createNewUser(email: String, password: String): MutableLiveData<AuthData> {
        val authenticatedUserLiveData = MutableLiveData<AuthData>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Created new user! ${task.result?.user?.email}")
                    authenticatedUserLiveData.postValue(AuthData(task.result?.user, null))

                    task.result?.user?.let {
                        DatabaseRepository.getDbInstance().collection("users")
                            .document(it.uid).set(it).addOnCompleteListener {
                                Log.d("AuthService", "Added new user to database.")
                            }
                    }
                } else {
                    Log.d(
                        "AuthService",
                        "Failed to create new user! $email ${task.exception?.message}"
                    )
                    authenticatedUserLiveData.postValue(
                        AuthData(null, task.exception?.message)
                    )
                }
            }

        return authenticatedUserLiveData
    }

    fun loginWithEmail(email: String, password: String): MutableLiveData<AuthData> {
        val userLiveData = MutableLiveData<AuthData>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Sign in with new user! $email")
                    userLiveData.value =
                        AuthData(task.result?.user, null)
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
}
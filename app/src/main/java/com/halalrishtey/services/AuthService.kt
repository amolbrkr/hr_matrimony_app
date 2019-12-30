package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthService {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): MutableLiveData<FirebaseUser> {

        val user = MutableLiveData<FirebaseUser>()

        if (firebaseAuth.currentUser != null) {
            user.value = firebaseAuth.currentUser
        } else {
            user.value = null
        }

        return user
    }

    fun createNewUser(email: String, password: String): MutableLiveData<FirebaseUser> {
        val authenticatedUserLiveData = MutableLiveData<FirebaseUser>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Created new user! $email")
                    authenticatedUserLiveData.value = task.result?.user

                    task.result?.user?.let {
                        DatabaseService.getDbInstance().collection("users")
                            .document(it.uid).set(it).addOnCompleteListener { task: Task<Void> ->
                                Log.d("AuthService", "Added new user to database.")
                            }
                    }
                } else {
                    Log.d(
                        "AuthService",
                        "Failed to create new user! $email ${task.exception?.message}"
                    )
                    authenticatedUserLiveData.value = null
                }
            }


        return authenticatedUserLiveData
    }

    fun loginWithEmail(email: String, password: String): MutableLiveData<FirebaseUser> {
        val userLiveData = MutableLiveData<FirebaseUser>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Sign in with new user! $email")
                    userLiveData.value = task.result?.user
                } else {
                    Log.d(
                        "AuthService",
                        "Failed to sign in! $email ${task.exception?.message}"
                    )
                    userLiveData.value = null
                }
            }

        return userLiveData
    }
}
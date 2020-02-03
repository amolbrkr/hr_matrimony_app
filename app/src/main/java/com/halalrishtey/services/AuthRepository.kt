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

    fun createNewUser(email: String, password: String, newUser: User): MutableLiveData<AuthData> {
        val authenticatedUserLiveData = MutableLiveData<AuthData>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Created new user! ${task.result?.user?.email}")
                    newUser.uid = task.result?.user?.uid
                    DatabaseRepository.getDbInstance().collection("users")
                        .document(newUser.uid!!).set(newUser).addOnCompleteListener {
                            Log.d("AuthService", "Added new user to database. ${newUser.email}")
                            if (it.isSuccessful) {
                                authenticatedUserLiveData.value = AuthData(newUser, null)
                            } else {
                                authenticatedUserLiveData.value =
                                    AuthData(null, "Cannot add new user to database!")
                            }
                        }
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

    fun addNewUserToDB(uid: String?, userData: User?): MutableLiveData<String?> {
        val msg = MutableLiveData<String?>()
        if (uid != null && userData != null) {
            DatabaseRepository.getDbInstance().collection("users")
                .document(uid).set(userData).addOnCompleteListener {
                    Log.d("AuthService", "Added new user to database. ${userData.email}")
                    if (it.isSuccessful) {
                        msg.value = "Successfully created!"
                    }
                }
        } else {
            msg.value = "Invalid or blank uid & userData!"
        }
        return msg
    }

    fun loginWithEmail(
        email: String,
        password: String,
        newUserInfo: HashMap<String, Any?>?
    ): MutableLiveData<AuthData> {
        val userLiveData = MutableLiveData<AuthData>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d("AuthService", "Sign in with new user! $email")
                    userLiveData.value =
                        AuthData(User(task.result?.user!!), null)

                    if (newUserInfo != null) {
                        updateUserData(task.result?.user?.uid!!, newUserInfo)
                    }
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

    fun updateUserData(userId: String, newData: HashMap<String, Any?>) {
        DatabaseRepository.getDbInstance()
            .collection("users").document(userId).update(newData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("AuthService", "Succesfully updated user data for $userId")
                } else {
                    Log.d("AuthService", "Failed to update data $userId, ${it.exception?.message}")
                }
            }
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
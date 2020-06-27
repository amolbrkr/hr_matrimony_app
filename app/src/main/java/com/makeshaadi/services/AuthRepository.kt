package com.makeshaadi.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.makeshaadi.models.AuthData
import com.makeshaadi.models.User

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
                    Log.d("AuthRepository", "Created new user! ${task.result?.user?.email}")
                    newUser.uid = task.result?.user?.uid
                    DatabaseService.getDbInstance().collection("users")
                        .document(newUser.uid!!).set(newUser).addOnCompleteListener {
                            Log.d("AuthRepository", "Added new user to database. ${newUser.email}")
                            if (it.isSuccessful) {
                                authenticatedUserLiveData.value = AuthData(newUser, null)
                            } else {
                                authenticatedUserLiveData.value =
                                    AuthData(null, "Cannot add new user to database!")
                            }
                        }
                } else {
                    Log.d(
                        "AuthRepository",
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
            DatabaseService.getDbInstance().collection("users")
                .document(uid).set(userData).addOnCompleteListener {
                    Log.d("AuthRepository", "Added new user to database. ${userData.email}")
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
                    Log.d("AuthRepository", "Sign in with new user! $email")
                    userLiveData.value =
                        AuthData(User(task.result?.user!!), null)

                    if (newUserInfo != null) {
                        updateUserData(task.result?.user?.uid!!, newUserInfo)
                    }
                } else {
                    Log.d(
                        "AuthRepository",
                        "Failed to sign in! $email ${task.exception?.message}"
                    )
                    userLiveData.value = AuthData(null, task.exception?.message)
                }
            }

        return userLiveData
    }

    fun updateUserData(userId: String, newData: Map<String, Any?>) {
        DatabaseService.getDbInstance()
            .collection("users").document(userId).update(newData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "AuthRepository",
                        "Succesfully updated user data for $userId, data: $newData"
                    )
                } else {
                    Log.d(
                        "AuthRepository",
                        "Failed to update data $userId, ${it.exception?.message}"
                    )
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
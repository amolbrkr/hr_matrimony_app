package com.halalrishtey.services

import com.google.firebase.firestore.FirebaseFirestore

object DatabaseRepository {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getDbInstance() = db
}
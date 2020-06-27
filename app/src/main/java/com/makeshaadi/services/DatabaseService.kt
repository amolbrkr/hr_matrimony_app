package com.makeshaadi.services

import com.google.firebase.firestore.FirebaseFirestore

object DatabaseService {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getDbInstance() = db
}
package com.halalrishtey

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.halalrishtey.models.Gender
import com.halalrishtey.models.User
import java.util.*

object CustomUtils {

    fun convertToUser(snapshot: DocumentSnapshot): User {
        return User(
            email = snapshot.get("email").toString(),
            uid = snapshot.get("uid").toString(),
            displayName = snapshot.get("displayName").toString(),
            age = snapshot.get("age").toString().toInt(),
            photoUrl = snapshot.get("photoUrl").toString(),
            idProofUrl = snapshot.get("idProofUrl").toString(),
            phoneNumber = snapshot.get("phoneNumber") as Number,
            gender = Gender.valueOf(snapshot.get("gender").toString()),
            createdFor = snapshot.get("createdFor").toString(),
            lastSignInAt = System.currentTimeMillis(),
            address = snapshot.get("address")?.toString() ?: "Not found",
            height = snapshot.get("height").toString(),
            education = snapshot.get("education").toString(),
            workLocation = snapshot.get("workLocation").toString(),
            sect = snapshot.get("sect").toString(),
            dargah = snapshot.get("dargah").toString(),
            maritalStatus = snapshot.get("maritalStatus").toString(),
            locationLat = snapshot.get("locationLat")?.toString()?.toDouble() ?: 0.0,
            locationLong = snapshot.get("locationLong")?.toString()?.toDouble()
                ?: 0.0,
            countryCode = snapshot.get("countryCode").toString(),
            pincode = snapshot.get("pincode")?.toString() ?: "Not found",
            isOTPVerified = snapshot.get("otpverified") as Boolean,
            countryCallingCode = snapshot.get("countryCallingCode").toString()
            //interestCount = it.get("interestCount") as Int
        )
    }

    fun convertCoordsToAddr(context: Context, latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            geocoder.getFromLocation(latitude, longitude, 1)[0]
        } catch (e: Exception) {
            Log.d("CustomUtils", "Error: ${e.message}")
            null
        }
    }
}
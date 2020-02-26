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

    fun convertToUser(doc: DocumentSnapshot): User {
        val tmp = doc.get("shortlistedProfiles")
        if (tmp != null) {
            val u = User(
                email = doc.get("email").toString(),
                uid = doc.get("uid").toString(),
                displayName = doc.get("displayName").toString(),
                age = doc.get("age").toString().toInt(),
                photoUrl = doc.get("photoUrl").toString(),
                idProofUrl = doc.get("idProofUrl").toString(),
                phoneNumber = doc.get("phoneNumber") as Number,
                gender = Gender.valueOf(doc.get("gender").toString()),
                createdFor = doc.get("createdFor").toString(),
                lastSignInAt = System.currentTimeMillis(),
                address = doc.get("address")?.toString() ?: "Not found",
                height = doc.get("height").toString(),
                education = doc.get("education").toString(),
                workLocation = doc.get("workLocation").toString(),
                sect = doc.get("sect").toString(),
                dargah = doc.get("dargah").toString(),
                maritalStatus = doc.get("maritalStatus").toString(),
                locationLat = doc.get("locationLat")?.toString()?.toDouble() ?: 0.0,
                locationLong = doc.get("locationLong")?.toString()?.toDouble()
                    ?: 0.0,
                countryCode = doc.get("countryCode").toString(),
                pincode = doc.get("pincode")?.toString() ?: "Not found",
                isOTPVerified = doc.get("otpverified") as Boolean,
                countryCallingCode = doc.get("countryCallingCode").toString(),
                shortlistedProfiles = tmp as ArrayList<String>
                //interestCount = it.get("interestCount") as Int
            )
            Log.d("CustomUtils", "Shortlisted Profiles: ${u.shortlistedProfiles}")
            return u
        } else {
            return User(
                email = doc.get("email").toString(),
                uid = doc.get("uid").toString(),
                displayName = doc.get("displayName").toString(),
                age = doc.get("age").toString().toInt(),
                photoUrl = doc.get("photoUrl").toString(),
                idProofUrl = doc.get("idProofUrl").toString(),
                phoneNumber = doc.get("phoneNumber") as Number,
                gender = Gender.valueOf(doc.get("gender").toString()),
                createdFor = doc.get("createdFor").toString(),
                lastSignInAt = System.currentTimeMillis(),
                address = doc.get("address")?.toString() ?: "Not found",
                height = doc.get("height").toString(),
                education = doc.get("education").toString(),
                workLocation = doc.get("workLocation").toString(),
                sect = doc.get("sect").toString(),
                dargah = doc.get("dargah").toString(),
                maritalStatus = doc.get("maritalStatus").toString(),
                locationLat = doc.get("locationLat")?.toString()?.toDouble() ?: 0.0,
                locationLong = doc.get("locationLong")?.toString()?.toDouble()
                    ?: 0.0,
                countryCode = doc.get("countryCode").toString(),
                pincode = doc.get("pincode")?.toString() ?: "Not found",
                isOTPVerified = doc.get("otpverified") as Boolean,
                countryCallingCode = doc.get("countryCallingCode").toString()
            )
        }
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
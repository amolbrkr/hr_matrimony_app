package com.halalrishtey

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.halalrishtey.models.Gender
import com.halalrishtey.models.User
import java.util.*


object CustomUtils {
    fun displayLocalNotif(
        context: Context,
        notifId: Int,
        tapOpenActivityClass: Class<*>,
        channelName: String,
        notifTitle: String,
        notifText: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = channelName.toLowerCase(Locale.ROOT).replace(" ", "_")

        val intent = Intent(context, tapOpenActivityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setShowBadge(true)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notifTitle)
            .setContentText(notifText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notifId, builder.build())
        }
    }

    fun convertToUser(doc: DocumentSnapshot): User {
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
            countryCallingCode = doc.get("countryCallingCode").toString(),
            interestedProfiles = doc.get("interestedProfiles") as ArrayList<String>
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
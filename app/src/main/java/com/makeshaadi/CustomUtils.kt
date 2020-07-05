package com.makeshaadi

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.PlanItem
import com.makeshaadi.models.ProfilePicVisibility
import com.makeshaadi.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object CustomUtils {

    fun getMeetupReciever(current: String, meetup: MeetupItem): String {
        return if (current == meetup.sourceId) meetup.targetId
        else current
    }

    fun getMeetupInitiator(current: String, meetup: MeetupItem): String {
        return if (current == meetup.sourceId) current
        else meetup.targetId
    }

    fun convertFtIntoCm(length: String): Int {
        val n = length.filter { it.isDigit() }
        return ("${n[0]}.${n.substring(1)}".toDouble() * 30.48).toInt()
    }

    fun convertCmToFtIn(lengthInCm: Int): String {
        val ft = (lengthInCm / 30.48).toString()[0]
        val inc = (lengthInCm / 30.48).toString().substring(2, 3)
        return "$ft ft $inc in"
    }

    fun convertMonthsToMillis(months: Int): Long {
        return months * 2592000000
    }

    fun openUserDetails(context: Context, userId: String) {
        val i = Intent(context, UserDetailActivity::class.java)
        i.putExtra("userId", userId)
        context.startActivity(i)
    }

    fun filterValidProfiles(currentUser: User, profiles: ArrayList<User>): List<User> {
        val r = profiles.filter { u -> u.gender != currentUser.gender }
            .filter { u -> u.uid != currentUser.uid }
            .filterNot { u -> currentUser.blockList.contains(u.uid) }

        Log.d("CustomUtils", "Valid profiles: $r")
        return r
    }

    fun genTimeString(timeInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMillis
        return dateFormatter.format(cal.time).toString()
    }

    fun genDateString(timeInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy hh:mm a", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMillis
        return dateFormatter.format(cal.time).toString()
    }

    fun genCombinedHash(str1: String, str2: String): String {
        val h = if (str1.length <= str2.length) str1.length / 2 else str2.length / 2
        val a = listOf(str1, str2).sorted()
        return a[0].substring(0, h) + a[1].substring(0, h)
    }

    @SuppressLint("WrongConstant")
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
                NotificationManager.IMPORTANCE_MAX
            )

            channel.lightColor = Color.WHITE
            channel.enableLights(true)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.vibrationPattern = longArrayOf(1000, 1000)
            channel.enableVibration(true)
            channel.setShowBadge(true)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setLargeIcon(BitmapFactory.decodeResource(null, R.drawable.ic_notif_icon))
            .setColor(Color.RED)
            .setContentTitle(notifTitle)
            .setContentText(notifText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(500, 700))
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
            annualIncome = doc.get("annualIncome").toString(),
            photoUrl = doc.get("photoUrl").toString(),
            idProofUrl = doc.get("idProofUrl").toString(),
            phoneNumber = doc.get("phoneNumber") as Number,
            gender = enumValueOf(doc.get("gender").toString()),
            createdFor = doc.get("createdFor").toString(),
            createdAt = doc.get("createdAt").toString().toLong(),
            lastSignInAt = System.currentTimeMillis(),
            address = doc.get("address")?.toString() ?: "Not found",
            height = doc.get("height").toString(),
            qualification = doc.get("qualification").toString(),
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
            isIdProofVerified = doc.get("idProofVerified").toString().toBoolean(),
            countryCallingCode = doc.get("countryCallingCode").toString(),
            interestedProfiles = doc.get("interestedProfiles") as ArrayList<String>,
            conversations = doc.get("conversations") as ArrayList<String>,
            blockList = doc.get("blockList") as ArrayList<String>,
            photoList = doc.get("photoList") as ArrayList<String>,
            organizationName = doc.get("organizationName").toString(),
            profilePicVisibility = ProfilePicVisibility.valueOf(
                doc.get("profilePicVisibility").toString()
            ),
            bio = doc.get("bio")?.toString() ?: "",
            fathersName = doc.get("fathersName").toString(),
            fathersJob = doc.get("fathersJob").toString(),
            numBrothers = doc.get("numBrothers").toString(),
            numSisters = doc.get("numSisters").toString(),
            meetupList = doc.get("meetupList") as ArrayList<String>,
            registrationToken = doc.get("registrationToken").toString(),
            dateOfBirth = doc.get("dateOfBirth").toString().toLong(),
            isSuspended = doc.get("suspended").toString().toBoolean(),
            planStart = doc.get("planStart").toString().toLong(),
            currentPlan = convertToPlan(
                doc.get("currentPlan") as Map<String, Any>?
            ),
            qualDetails = doc.get("qualDetails").toString(),
            directContacts = doc.get("directContacts") as ArrayList<String>
        )
    }

    fun convertToPlan(plan: Map<String, Any>?): PlanItem? {
        val p = if (plan != null) {
            PlanItem(
                id = plan["id"].toString(),
                actualPrice = plan["actualPrice"].toString(),
                chatCount = plan["chatCount"].toString().toInt(),
                dcCount = plan["dcCount"].toString().toInt(),
                discountPrice = plan["discountPrice"].toString().toInt(),
                meetupCount = plan["meetupCount"].toString().toInt(),
                name = plan["name"].toString(),
                validity = plan["validity"].toString().toInt()
            )
        } else null

        Log.d("CustomUtils", "Converted Plan: $p")
        return p
    }

    fun convertToPlan(d: DocumentSnapshot): PlanItem {
        return PlanItem(
            id = d["id"].toString(),
            actualPrice = d["actualPrice"].toString(),
            chatCount = d["chatCount"].toString().toInt(),
            dcCount = d["dcCount"].toString().toInt(),
            discountPrice = d["discountPrice"].toString().toInt(),
            meetupCount = d["meetupCount"].toString().toInt(),
            name = d["name"].toString(),
            validity = d["validity"].toString().toInt()
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
package com.makeshaadi


import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("FCMService", "New token: $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if (p0.notification != null) {
            CustomUtils.displayLocalNotif(
                this,
                777,
                MainActivity::class.java,
                "ms_notif",
                p0.notification?.title!!,
                p0.notification?.body.toString()
            );
        }
    }
}
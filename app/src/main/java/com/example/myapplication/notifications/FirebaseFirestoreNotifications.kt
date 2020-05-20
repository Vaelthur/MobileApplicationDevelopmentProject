package com.example.myapplication.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class FirebaseFirestoreNotifications : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val body : String? = remoteMessage.notification?.body
        Log.d("MSG", body!!)
        showNotification(remoteMessage.notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MSG", token)
    }


    fun showNotification(notification: RemoteMessage.Notification?) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "com.example.myapplication"

        //Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.let {
                it.description = "SellBackApp"
                it.enableLights(true)
                it.lightColor = Color.BLUE
                notificationManager.createNotificationChannel(it)
            }
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.apply {
            setAutoCancel(true)
            setWhen(System.currentTimeMillis())
            setContentTitle(notification?.title)
            setContentText(notification?.body)
            setContentInfo("Info")
            setSmallIcon(R.drawable.ic_stat_name)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }


}
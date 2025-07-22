package com.example.zdravlje.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.zdravlje.ui.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.nites.ezdravljeapp.R

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CloudMessagingService : FirebaseMessagingService() {
    private val TAG = CloudMessagingService::class.java.simpleName
    private val CHANNEL_ID = "fcm_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val gson = Gson()
        val jsonRemoteMessage = gson.toJson(remoteMessage.data)
        Log.i(TAG, "Primljena poruka: $jsonRemoteMessage")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Nova poruka"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Imate novu notifikaciju"

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Obaveštenja",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kanal za Firebase obaveštenja"
                enableLights(true)
                enableVibration(true)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo) // PROVERI DA LI POSTOJI
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

package com.example.doccur.websocket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.doccur.entities.Notification
import com.example.doccur.MainActivity

class NotificationChannelManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "doccur_notifications"
        const val CHANNEL_NAME = "Doccur Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications from Doccur application"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notification: Notification) {
        // Create an intent that will be triggered when the user taps the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTIFICATION_ID", notification.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notification.title ?: "New Message")
            .setContentText(notification.message ?: "You have a new notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notification.id, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
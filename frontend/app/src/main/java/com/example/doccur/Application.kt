package com.example.doccur

import android.app.Application
import com.example.doccur.websocket.NotificationChannelManager

class DoccurApplication : Application() {

    lateinit var notificationChannelManager: NotificationChannelManager
    override fun onCreate() {
        super.onCreate()
        notificationChannelManager = NotificationChannelManager(applicationContext)
    }
}
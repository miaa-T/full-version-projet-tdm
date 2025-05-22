package com.example.doccur.websocket

import android.content.Context
import android.util.Log
import com.example.doccur.DoccurApplication
import com.example.doccur.entities.Notification
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

class NotificationWebSocketClient(
    private val baseUrl: String,
    private val userType: String,
    private val userId: Int,
    private val context: Context? = null  // Add Context parameter (nullable)
) {
    private val TAG = "WebSocketClient"
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    // Shared flow to emit notifications
    private val _notificationFlow = MutableSharedFlow<Notification>(replay = 0)
    val notificationFlow: SharedFlow<Notification> = _notificationFlow

    init {
        client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun connect() {
        val wsUrl = "$baseUrl/ws/notifications/$userId/$userType/"
        Log.d(TAG, "Connecting to WebSocket URL: $wsUrl")
        val request = Request.Builder()
            .url(wsUrl)
            .build()

        client?.let {
            webSocket = it.newWebSocket(request, createWebSocketListener())
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    fun markNotificationAsRead(notificationId: Int) {
        val message = gson.toJson(mapOf(
            "type" to "read_notification",
            "notification_id" to notificationId
        ))
        webSocket?.send(message)
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connection opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                try {
                    val jsonObject = JsonParser.parseString(text).asJsonObject
                    val type = jsonObject.get("type").asString

                    if (type == "notification") {
                        val notificationJson = jsonObject.getAsJsonObject("notification")
                        val notification = Notification(
                            id = notificationJson.get("id").asInt,
                            title = notificationJson.get("title").asString,
                            message = notificationJson.get("message").asString,
                            isRead = false,
                        )

                        // Emit notification to flow for UI updates
                        CoroutineScope(Dispatchers.Main).launch {
                            _notificationFlow.emit(notification)
                        }

                        // Show system notification if context is available
                        context?.let { ctx ->
                            try {
                                val app = ctx.applicationContext as DoccurApplication
                                app.notificationChannelManager.showNotification(notification)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error showing notification: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing notification: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                // Retry connection after a delay
                CoroutineScope(Dispatchers.IO).launch {
                    kotlinx.coroutines.delay(5000)
                    connect()
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
            }
        }
    }
}
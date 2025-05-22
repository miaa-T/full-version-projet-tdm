package com.example.doccur.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Notification
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.websocket.NotificationWebSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val context: Context,  // Add Context parameter
    private val wsBaseUrl: String = "ws://172.20.10.4:8000"
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var wsClient: NotificationWebSocketClient? = null

    fun connectWebSocket(userId: Int, userType: String) {
        // Pass context to WebSocketClient
        wsClient = NotificationWebSocketClient(wsBaseUrl, userType, userId, context)

        viewModelScope.launch {
            wsClient?.notificationFlow?.collect { notification ->
                // Add the new notification to the list
                val currentList = _notifications.value.toMutableList()
                // Check if the notification doesn't already exist
                if (currentList.none { it.id == notification.id }) {
                    currentList.add(0, notification) // Add at the beginning of the list
                    _notifications.value = currentList
                }
            }
        }

        wsClient?.connect()
    }

    fun disconnectWebSocket() {
        wsClient?.disconnect()
        wsClient = null
    }

    fun fetchNotifications(userId: Int, userType: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getNotifications(userId, userType)
                _notifications.value = result
                _error.value = null

                // Connect to WebSocket after fetching existing notifications
                connectWebSocket(userId, userType)
            } catch (e: Exception) {
                _error.value = "Failed to load notifications: ${e.message}"
                _notifications.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.markNotificationAsRead(notificationId)
                if (success) {
                    // Update the local list of notifications
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }

                    // Inform the server via WebSocket that the notification is read
                    wsClient?.markNotificationAsRead(notificationId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to mark notification as read: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}
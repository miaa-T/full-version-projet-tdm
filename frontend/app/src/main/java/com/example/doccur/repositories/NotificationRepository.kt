package com.example.doccur.repositories

import com.example.doccur.entities.Notification
import com.example.doccur.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(private val apiService: ApiService) {

    suspend fun getNotifications(userId: Int, userType: String): List<Notification> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getNotifications(userId, userType)
            if (response.isSuccessful) {
                response.body()?.notifications ?: emptyList()
            } else {
                // Handle error
                emptyList()
            }
        }
    }

    suspend fun markNotificationAsRead(notificationId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.markNotificationAsRead(notificationId)
            response.isSuccessful
        }
    }
}
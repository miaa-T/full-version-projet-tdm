package com.example.doccur.entities


data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    var isRead: Boolean = false
)

data class NotificationsResponse(
    val notifications: List<Notification>
)

data class MarkReadResponse(
    val message: String
)
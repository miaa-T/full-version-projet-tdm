package com.example.doccur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doccur.api.ApiService
import com.example.doccur.entities.Notification
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory

val customTextStyle = TextStyle(
    fontFamily = Inter,
)

@Composable
fun NotificationsScreen(
    viewModel: NotificationViewModel,
    userId: Int,
    userType: String,
    wsBaseUrl: String = "ws://172.20.10.4:8000"
) {

    // Collect state from ViewModel
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(key1 = userId, key2 = userType) {
        viewModel.fetchNotifications(userId, userType)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.disconnectWebSocket()
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides customTextStyle
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with title and "Mark all as read" button
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp,
                backgroundColor = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal=24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    TextButton(onClick = {
                        // Mark all notifications as read
                        notifications.forEach { notification ->
                            if (!notification.isRead) {
                                viewModel.markAsRead(notification.id)
                            }
                        }
                    }) {
                        Text(
                            text = "Mark all as read",
                            color = AppColors.Blue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                // Show loading spinner if loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Show error if there's an error
                error?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                // Show notifications list
                if (!isLoading && error == null) {
                    if (notifications.isEmpty()) {
                        // Show empty state
                        Text(
                            text = "No notifications",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        // Show notifications list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(notifications) { notification ->
                                // Determine icon based on content
                                val message = notification.message.orEmpty()

                                val (icon, iconBackgroundColor) = when {
                                    // Doctor confirmed appointment
                                    message.contains("confirm", ignoreCase = true) ->
                                        Pair(Icons.Default.CheckCircle, AppColors.Green)

                                    // Doctor rejected appointment
                                    message.contains("reject", ignoreCase = true) ->
                                        Pair(Icons.Default.Cancel, Color(0xFFB91C1C))

                                    // Patient canceled appointment
                                    message.contains("cancel", ignoreCase = true) ->
                                        Pair(Icons.Default.Cancel, Color(0xFFB91C1C))

                                    // Patient rescheduled appointment
                                    message.contains("reschedule", ignoreCase = true) ->
                                        Pair(Icons.Default.EventRepeat, Color(0xFFB91C1C))

                                    // Default fallback
                                    else ->
                                        Pair(Icons.Default.Notifications, Color(0xFFE3E3E3))
                                }

                                NotificationItem(
                                    notification = notification,
                                    icon = icon,
                                    iconColor = iconBackgroundColor,
                                    isUnread = !notification.isRead,
                                    onClick = { viewModel.markAsRead(notification.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// NotificationItem composable remains the same
@Composable
fun NotificationItem(
    notification: Notification,
    icon: ImageVector,
    iconColor: Color,
    isUnread: Boolean,
    onClick: () -> Unit
) {

    CompositionLocalProvider(
        LocalTextStyle provides customTextStyle
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White,
                border = BorderStroke(0.2.dp, AppColors.LightGray),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Notification icon with background
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Notification content
                    Column(modifier = Modifier.weight(1f)) {
                        // Add notification title
                        notification.title?.let { title ->
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = notification.message ?: "No message available",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Unread indicator
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppColors.Blue)
                        )
                    }
                }
            }
        }
    }
}
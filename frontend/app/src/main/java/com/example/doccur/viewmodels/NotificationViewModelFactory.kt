package com.example.doccur.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repositories.NotificationRepository

class NotificationViewModelFactory(
    private val repository: NotificationRepository,
    private val context: Context,
    private val wsBaseUrl: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repository, context, wsBaseUrl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
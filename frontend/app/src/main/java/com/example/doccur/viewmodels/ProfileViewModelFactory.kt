package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.repositories.ProfileRepository

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
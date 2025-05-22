package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repositories.ProfileRepository
import com.example.doccur.repositories.UsersRepository

class UsersViewModelFactory(
    private val repository: UsersRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
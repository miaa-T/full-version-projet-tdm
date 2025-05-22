package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repositories.AppointmentRepository

class AppointmentViewModelFactory
    (private val repository: AppointmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            return AppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


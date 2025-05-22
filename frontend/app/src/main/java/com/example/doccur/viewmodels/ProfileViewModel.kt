package com.example.doccur.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.repositories.ProfileRepository
import kotlinx.coroutines.launch
import com.example.doccur.entities.DoctorProfile

class ProfileViewModel(private val doctorRepository: ProfileRepository) : ViewModel() {

    var doctor by mutableStateOf<DoctorProfile?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun getDoctorDetails(doctorId: Int) {
        isLoading = true
        viewModelScope.launch {
            try {
                doctor = doctorRepository.getDoctorDetails(doctorId)
                error = null
            } catch (e: Exception) {
                error = e.message ?: "Unknown error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
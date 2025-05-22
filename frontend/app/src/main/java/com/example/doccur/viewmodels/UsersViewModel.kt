package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Doctor2
import com.example.doccur.entities.DoctorDetails
import com.example.doccur.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: UsersRepository
) : ViewModel() {

    private val _doctorList = MutableStateFlow<List<Doctor2>>(emptyList())
    val doctorList: StateFlow<List<Doctor2>> = _doctorList

    private val _selectedDoctor = MutableStateFlow<DoctorDetails?>(null)
    val selectedDoctor: StateFlow<DoctorDetails?> = _selectedDoctor

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadDoctors() {
        viewModelScope.launch {
            try {
                val doctors = repository.fetchDoctors()
                _doctorList.value = doctors
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load doctors: ${e.localizedMessage}"
            }
        }
    }

    fun loadDoctorDetails(id: Int) {
        viewModelScope.launch {
            try {
                val doctorDetails = repository.fetchDoctorDetails(id)
                _selectedDoctor.value = doctorDetails
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load doctor details: ${e.localizedMessage}"
            }
        }
    }
}

package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.DoctorStatisticsResponse
import com.example.doccur.entities.PatientStatisticsResponse
import com.example.doccur.repositories.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _doctorStats = MutableStateFlow<DoctorStatisticsResponse?>(null)
    val doctorStats: StateFlow<DoctorStatisticsResponse?> = _doctorStats

    private val _patientStats = MutableStateFlow<PatientStatisticsResponse?>(null)
    val patientStats: StateFlow<PatientStatisticsResponse?> = _patientStats

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchDoctorStatistics(doctorId: Int) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val stats = repository.getDoctorStats(doctorId)
                _doctorStats.value = stats
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun fetchPatientStatistics(patientId: Int) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val stats = repository.getPatientStats(patientId)
                _patientStats.value = stats
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

}
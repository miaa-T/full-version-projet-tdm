package com.example.doccur.repositories

import com.example.doccur.api.ApiService
import com.example.doccur.entities.DoctorStatisticsResponse
import com.example.doccur.entities.PatientStatisticsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(private val apiService: ApiService) {

    suspend fun getDoctorStats(doctorId: Int): DoctorStatisticsResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getDoctorStatistics(doctorId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                throw Exception("Failed to fetch doctor statistics: $errorMsg")
            }
        }
    }

    suspend fun getPatientStats(patientId: Int): PatientStatisticsResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getPatientStatistics(patientId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                throw Exception("Failed to fetch doctor statistics: $errorMsg")
            }
        }
    }

}
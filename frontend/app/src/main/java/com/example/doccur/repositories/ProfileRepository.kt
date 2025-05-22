package com.example.doccur.repositories

import com.example.doccur.api.ApiService
import com.example.doccur.entities.DoctorProfile

class ProfileRepository(private val apiService: ApiService) {
    suspend fun getDoctorDetails(doctorId: Int): DoctorProfile? {
        val response = apiService.getDoctorProfile(doctorId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}
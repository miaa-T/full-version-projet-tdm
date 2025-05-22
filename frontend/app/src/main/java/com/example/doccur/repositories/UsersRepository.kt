package com.example.doccur.repositories

import com.example.doccur.api.ApiService
import com.example.doccur.entities.Doctor2
import com.example.doccur.entities.DoctorDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersRepository(private val apiService: ApiService) {

    suspend fun fetchDoctors(): List<Doctor2> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getDoctors()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty doctor list")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun fetchDoctorDetails(id: Int): DoctorDetails {
        return withContext(Dispatchers.IO) {
            val response = apiService.getDoctorDetails(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty doctor details")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }
}

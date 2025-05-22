package com.example.doccur.repositories

import android.util.Log
import com.example.doccur.api.ApiResponse
import com.example.doccur.api.ApiService
import com.example.doccur.api.AppointmentBookRequest
import com.example.doccur.api.RejectReasonRequest
import com.example.doccur.entities.AppointmentBookResponse
import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.CancelAppointmentResponse
import com.example.doccur.entities.ConfirmAppointmentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository(private val apiService: ApiService) {


    suspend fun scanQrCode(appointmentId: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.scanQrCode(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun getFullAppointmentsForDoctor(doctorId: Int): List<AppointmentPatient> {
        return try {
            val response = apiService.getFullAppointmentsByDoctor(doctorId)
            response
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFullAppointmentsForPatient(patientId: Int): List<AppointmentPatient> {
        val response = apiService.getFullAppointmentsByPatient(patientId)
        response.forEachIndexed { index, appointment ->
            Log.d("Repository", "Appointment #$index: $appointment")
        }
        return response
    }




    suspend fun getAppointmentDetails(appointmentId: Int): AppointmentDetailsResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getAppointmentDetails(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun bookAppointment(request: AppointmentBookRequest): AppointmentBookResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.bookAppointment(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }


    suspend fun confirmAppointment(appointmentId: Int): ConfirmAppointmentResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.confirmAppointment(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun rejectAppointment(appointmentId: Int, reason: String): String {
        return withContext(Dispatchers.IO) {
            val response = apiService.rejectAppointment(appointmentId, RejectReasonRequest(reason))
            if (response.isSuccessful) {
                response.body()?.message ?: throw Exception("Empty response message")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun cancelAppointment(appointmentId: Int): CancelAppointmentResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.cancelAppointment(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

}

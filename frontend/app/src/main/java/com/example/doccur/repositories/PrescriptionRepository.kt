package com.example.doccur.repositories


import android.util.Log
import com.example.doccur.api.ApiService
import com.example.doccur.entities.Prescription

class PrescriptionRepository(private val apiService: ApiService) {
    suspend fun checkPrescriptionExists(appointmentId: Int): Boolean {
        Log.d("PrescriptionRepo", "🟡 Checking prescription for appointment: $appointmentId")

        return try {
            Log.i("PrescriptionRepo", "🔵 Sending API request for appointment: $appointmentId")
            val response = apiService.getPrescriptionByAppointmentId(appointmentId)

            if (response.isSuccessful) {
                Log.d("PrescriptionRepo", "🟢 Prescription found for appointment: $appointmentId")
                Log.v("PrescriptionRepo", "Response code: ${response.code()}, Body: ${response.body()}")
            } else {
                Log.w("PrescriptionRepo", "🟠 No prescription found for appointment: $appointmentId")
                Log.v("PrescriptionRepo", "Error code: ${response.code()}, Message: ${response.errorBody()?.string()}")
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PrescriptionRepo", "🔴 Error checking prescription for appointment: $appointmentId", e)
            Log.wtf("PrescriptionRepo", "CRITICAL ERROR: ${e.localizedMessage}")
            false
        }
    }
}
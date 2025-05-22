package com.example.doccur.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.api.ApiResponse
import com.example.doccur.api.AppointmentBookRequest
import com.example.doccur.api.RetrofitClient
import com.example.doccur.entities.AppointmentBookResponse
import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.repositories.AppointmentRepository
import com.example.doccur.repositories.PrescriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AppointmentViewModel(
    private val repository: AppointmentRepository,
    ) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentPatient>>(emptyList())
    val appointments: StateFlow<List<AppointmentPatient>> = _appointments

    private val _appointmentsForPatient = MutableStateFlow<List<AppointmentPatient>>(emptyList())
    val appointmentsForPatient: StateFlow<List<AppointmentPatient>> = _appointmentsForPatient

    private val _appointmentDetails = MutableStateFlow<AppointmentDetailsResponse?>(null)
    val appointmentDetails: StateFlow<AppointmentDetailsResponse?> = _appointmentDetails

    private val _appointmentBookingResult = MutableStateFlow<AppointmentBookResponse?>(null)
    val appointmentBookingResult: StateFlow<AppointmentBookResponse?> = _appointmentBookingResult


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _confirmationMessage = MutableStateFlow<String?>(null)
    val confirmationMessage: StateFlow<String?> = _confirmationMessage

    private val _cancelMessage = MutableStateFlow<String?>(null)
    val cancelMessage: StateFlow<String?> = _cancelMessage


    fun fetchAppointmentsForDoctor(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val result = repository.getFullAppointmentsForDoctor(doctorId)
                println(result)
                Log.d("ApptViewModeeel", "🟡doctooooor appointment: $result")

                _appointments.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchAppointmentsForPatient(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val result = repository.getFullAppointmentsForPatient(patientId)
                _appointmentsForPatient.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }



    fun fetchAppointmentDetails(appointmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val details = repository.getAppointmentDetails(appointmentId)
                _appointmentDetails.value = details
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun bookAppointment(patientId: Int, doctorId: Int, date: String, time: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _appointmentBookingResult.value = null

            try {
                val request = AppointmentBookRequest(
                    patientId = patientId,
                    doctorId = doctorId,
                    date = date,
                    time = time
                )
                val response = repository.bookAppointment(request)
                _appointmentBookingResult.value = response
                _confirmationMessage.value = response.message
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _confirmationMessage.value = null
            try {
                val response = repository.confirmAppointment(appointmentId)
                _confirmationMessage.value = response.message
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun rejectAppointment(appointmentId: Int, reason: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _confirmationMessage.value = null
            try {
                val message = repository.rejectAppointment(appointmentId, reason)
                _confirmationMessage.value = message
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelAppointment(appointmentId: Int, patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _cancelMessage.value = null

            try {
                val response = repository.cancelAppointment(appointmentId)
                _cancelMessage.value = response.message

                // After successful cancellation, refresh the appointments list
                fetchAppointmentsForPatient(patientId)
            } catch (e: Exception) {
                _error.value = e.message
                _loading.value = false // Make sure loading is set to false in case of error
            }
            // Note: we don't set loading to false here as fetchAppointmentsForPatient will handle that
        }
    }

    fun scanQrCode(appointmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _confirmationMessage.value = null
            try {
                val response = repository.scanQrCode(appointmentId)
                _confirmationMessage.value = response.message ?: "Appointment checked-in successfully"
                _error.value = "Failed to check-in appointment"
                fetchAppointmentDetails(appointmentId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to process QR code"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearConfirmationMessage() {
        _confirmationMessage.value = null
    }

    fun clearCancelMessage() {
        _cancelMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }

}

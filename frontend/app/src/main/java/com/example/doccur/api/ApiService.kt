package com.example.doccur.api

import com.example.doccur.entities.Appointment
import com.example.doccur.entities.AppointmentBookResponse
import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.CancelAppointmentResponse
import com.example.doccur.entities.ConfirmAppointmentResponse
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Doctor2
import com.example.doccur.entities.DoctorDetails
import com.example.doccur.entities.DoctorProfile
import com.example.doccur.entities.DoctorStatisticsResponse
import com.example.doccur.entities.MarkReadResponse
import com.example.doccur.entities.NotificationsResponse
import com.example.doccur.entities.Patient
import com.example.doccur.entities.PatientStatisticsResponse
import com.example.doccur.entities.Prescription
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //Notifications
    @GET("notifications/{userId}/{userType}/")
    suspend fun getNotifications(
        @Path("userId") userId: Int,
        @Path("userType") userType: String
    ): Response<NotificationsResponse>

    @POST("notifications/read/{notificationId}/")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: Int
    ): Response<MarkReadResponse>

    @GET("appointments/doctor/{doctorId}/statistics/")
    suspend fun getDoctorStatistics(
        @Path("doctorId") doctorId: Int
    ): Response<DoctorStatisticsResponse>

    @GET("appointments/patient/{patientId}/statistics/")
    suspend fun getPatientStatistics(
        @Path("patientId") patientId: Int
    ): Response<PatientStatisticsResponse>

    @GET("appointments/{appointment_id}/")
    suspend fun getAppointmentDetails(
        @Path("appointment_id") appointmentId: Int
    ): Response<AppointmentDetailsResponse>

    @POST("appointments/book/")
    suspend fun bookAppointment(
        @Body request: AppointmentBookRequest
    ): Response<AppointmentBookResponse>

    @POST("appointments/{appointment_id}/confirm/")
    suspend fun confirmAppointment(
        @Path("appointment_id") appointmentId: Int
    ): Response<ConfirmAppointmentResponse>

    @POST("appointments/{appointment_id}/reject/")
    suspend fun rejectAppointment(
        @Path("appointment_id") appointmentId: Int,
        @Body reasonBody: RejectReasonRequest
    ): Response<ConfirmAppointmentResponse>

    @POST("appointments/{appointment_id}/cancel/")
    suspend fun cancelAppointment(
        @Path("appointment_id") appointmentId: Int,
    ): Response<CancelAppointmentResponse>

    @POST("appointments/scan/{appointment_id}/")
    suspend fun scanQrCode(
        @Path("appointment_id") appointmentId: Int
    ): Response<ApiResponse>

    @GET("doctors/{doctor_id}/")
    suspend fun getDoctorProfile(
        @Path("doctor_id") doctorId: Int): Response<DoctorProfile>

    @GET("appointments/doctor/{doctor_id}/appointments/full/")
    suspend fun getFullAppointmentsByDoctor(
        @Path("doctor_id") doctorId: Int
    ): List<AppointmentPatient>

    @GET("appointments/patient/{patient_id}/appointments/full/")
    suspend fun getFullAppointmentsByPatient(@Path("patient_id") patientId: Int): List<AppointmentPatient>

    //List of doctors
    @GET("doctors/")
    suspend fun getDoctors(): Response<List<Doctor2>>

    @GET("doctors/{id}/")
    suspend fun getDoctorDetails(@Path("id") doctorId: Int): Response<DoctorDetails>

    @GET("prescriptions/appointment/{appointment_id}/")
    suspend fun getPrescriptionByAppointmentId(
        @Path("appointment_id") appointmentId: Int
    ): Response<Prescription>


}


data class RejectReasonRequest(val reason: String)

data class ApiResponse(
    @SerializedName("message")
    val message: String? = null,
)

data class AppointmentBookRequest(
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("doctor_id") val doctorId: Int,
    val date: String,  // Format: "yyyy-MM-dd"
    val time: String   // Format: "HH:mm"
)

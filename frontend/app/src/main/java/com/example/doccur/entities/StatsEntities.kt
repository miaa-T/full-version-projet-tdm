package com.example.doccur.entities

import com.google.gson.annotations.SerializedName

data class DoctorStatisticsResponse(
    val total_appointments_today: Int,
    val upcoming_appointment: AppointmentDto?,
    val completed_appointments: Int,
    val patients_attended: Int,
    val pending_appointments: Int
)

data class AppointmentDto(
    val id: Int,
    val doctor: Int,
    val patient: Int,
    val patient_first_name: String,
    val patient_last_name: String,
    val patient_date_birth: String,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String
)

data class PatientStatisticsResponse(
    @SerializedName("total_appointments_today")
    val totalAppointmentsToday: Int,

    @SerializedName("upcoming_appointment")
    val upcomingAppointment: AppointmentStats?,

    @SerializedName("completed_appointments")
    val completedAppointments: Int,

    @SerializedName("doctors_consulted")
    val doctorsConsulted: List<ConsultedDoctor>,

    @SerializedName("pending_appointments")
    val pendingAppointments: Int
)

data class AppointmentStats(
    val id: Int,
    val doctor: Int,

    @SerializedName("doctor_first_name")
    val doctorFirstName: String,

    @SerializedName("doctor_last_name")
    val doctorLastName: String,

    @SerializedName("doctor_specialty")
    val doctorSpecialty: String,

    @SerializedName("clinic_name")
    val clinicName: String,

    val date: String,   // ISO format: "YYYY-MM-DD"
    val time: String,   // Format: "HH:MM:SS"

    val status: String,

    @SerializedName("qr_code")
    val qrCode: String?
)

data class ConsultedDoctor(
    val id: Int,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val email: String,
    val specialty: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("clinic_name")
    val clinicName: String
)
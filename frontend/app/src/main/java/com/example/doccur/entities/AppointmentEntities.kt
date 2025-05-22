package com.example.doccur.entities

import com.google.gson.annotations.SerializedName

data class Appointment(
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String
)


data class AppointmentDetailsResponse(
    val id: Int,
    val doctor: String,
    val patient: PatientInfo,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String?,
    @SerializedName("has_prescription")
    val hasPrescription: Boolean
)

data class PatientInfo(
    val full_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String
)

data class ConfirmAppointmentResponse(
    val message: String,
    val qr_code: String,
    val qr_data: QrData
)

data class CancelAppointmentResponse(
    val message: String,
)

data class QrData(
    val appointment_id: Int,
    val doctor: String,
    val patient: String,
    val date: String,
    val time: String
)


data class AppointmentResponse(
    val id: Int,
    val date: String,
    val time: String,
    val status: String,
    @SerializedName("qr_code")
    val qrCode: String?,
    val doctor: Int,
    val patient: PatientData,
    val hasPrescription: Boolean
)

data class AppointmentPatient(
    val id: Int,
    val date: String,
    val time: String,
    val status: String,
    @SerializedName("qr_code")
    val qrCode: String?,
    val doctor: DoctorData,
    val patient: PatientData,
    @SerializedName("has_prescription")
    val hasPrescription: Boolean
)

data class PatientData(
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
)



data class DoctorData(
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    val speciality: String,
    @SerializedName("profile_image")
    val profileImage: String
)

data class AppointmentBookResponse(
    val message: String,
    @SerializedName("appointment_id") val appointmentId: Int,
    val status: String
)


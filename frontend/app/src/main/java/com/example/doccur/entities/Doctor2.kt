package com.example.doccur.entities

data class Doctor2(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val specialty: String,
    val photo_url: String,
    val clinic: Clinic?
)


data class DoctorDetails(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val specialty: String,
    val photo_url: String,
    val clinic: Clinic?,
    val facebook_link: String?,
    val instagram_link: String?,
    val twitter_link: String?,
    val linkedin_link: String?,
    val timeslots: List<Timeslot>
)

data class Timeslot(
    val id: Int,
    val start_time: String,
    val end_time: String,
    val date: String,
    val is_booked: Boolean
)
package com.example.doccur.entities


data class Patient(
    val id: Int,
    val firstName: String,
    val lastName: String
)

{
    val fullName: String
        get() = "Dr. $firstName $lastName"
}
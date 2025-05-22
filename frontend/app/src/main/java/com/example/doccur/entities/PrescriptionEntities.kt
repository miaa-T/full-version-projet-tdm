package com.example.doccur.entities

data class Prescription(
    val id: Int,
    val appointment: Int,
    val medications: List<Medication>,
    val issued_date: String
)

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val instructions: String
)

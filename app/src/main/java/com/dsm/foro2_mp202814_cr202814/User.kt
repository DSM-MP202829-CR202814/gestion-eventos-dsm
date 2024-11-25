package com.dsm.foro2_mp202814_cr202814

data class User(
    val id: String? = null, // ID del evento generado por Firebase
    val nombres: String,
    val apellidos: String,
    val email: String,
    val isAdmin: Boolean
)

data class RegisterResponse(
    val message: String,
    val id: String
)
package com.dsm.foro2_mp202814_cr202814

data class Event(
    val id: String? = null, // ID del evento generado por Firebase
    val nombre: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val descripcion: String
)
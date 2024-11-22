package com.dsm.foro2_mp202814_cr202814

import retrofit2.Call
import retrofit2.http.*

interface EventApi {
    // Obtener todos los eventos
    @GET("events")
    fun getEvents(): Call<List<Event>>

    // Crear un nuevo evento
    @POST("events")
    fun createEvent(@Body event: Event): Call<Event>

    // Actualizar un evento existente
    @PUT("events/{id}")
    fun updateEvent(@Path("id") id: String, @Body event: Event): Call<Event>
}
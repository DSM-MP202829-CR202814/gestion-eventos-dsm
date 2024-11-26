package com.dsm.foro2_mp202814_cr202814

import retrofit2.Call
import retrofit2.http.*

interface EventApi {
    // Obtener todos los eventos
    @GET("events")
    fun getEvents(): Call<List<Event>>

    @GET("events/{id}")
    fun getEventById(@Path("id") eventId: String): Call<Event>

    // Crear un nuevo evento
    @POST("events")
    fun createEvent(@Body event: EventRequest): Call<Event>

    // Actualizar un evento existente
    @PUT("events/{id}")
    fun updateEvent(@Path("id") id: String, @Body event: EventRequest): Call<Event>
}
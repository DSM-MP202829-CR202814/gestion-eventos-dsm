package com.dsm.foro2_mp202814_cr202814.api

import com.dsm.foro2_mp202814_cr202814.RegisterResponse
import com.dsm.foro2_mp202814_cr202814.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @POST("users")
    fun registerUser(@Body user: User): Call<RegisterResponse>

    @GET("users/userByEmail/{email}")
    fun getUserByEmail(@Path("email") email: String): Call<User>
}
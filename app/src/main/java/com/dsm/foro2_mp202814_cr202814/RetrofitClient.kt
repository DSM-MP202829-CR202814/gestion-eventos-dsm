package com.dsm.foro2_mp202814_cr202814

import com.dsm.foro2_mp202814_cr202814.api.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8383/"

    val instance: EventApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApi::class.java)
    }

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    // Configuración personalizada de OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para establecer conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de espera para leer datos
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de espera para escribir datos
        .build()
}
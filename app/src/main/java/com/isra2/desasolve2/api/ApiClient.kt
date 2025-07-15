package com.isra2.desasolve2.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URLs de la API
    // DESARROLLO (localhost)
    private const val BASE_URL = "http://localhost:8000/api/"
    
    // PRODUCCIÓN (DigitalOcean) - Cambia por tu dominio real
    // private const val BASE_URL = "https://tu-dominio.com/api/"
    // private const val BASE_URL = "https://desasolve-api.com/api/"
    // private const val BASE_URL = "https://api.desasolve.com/api/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 
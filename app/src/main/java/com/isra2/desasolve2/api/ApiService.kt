package com.isra2.desasolve2.api

import com.isra2.desasolve2.models.Quote
import com.isra2.desasolve2.models.Service
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("quotes/")
    suspend fun getQuotes(): Response<List<Quote>>
    
    @GET("quotes/{id}/")
    suspend fun getQuote(@Path("id") id: String): Response<Quote>
    
    @POST("quotes/{id}/accept/")
    suspend fun acceptQuote(@Path("id") id: String): Response<Quote>
    
    @POST("quotes/{id}/reject/")
    suspend fun rejectQuote(@Path("id") id: String): Response<Quote>
    
    @GET("services/")
    suspend fun getServices(): Response<List<Service>>
    
    @GET("services/{id}/")
    suspend fun getService(@Path("id") id: String): Response<Service>
    
    @POST("services/")
    suspend fun createService(@Body service: Service): Response<Service>
    
    @PUT("services/{id}/")
    suspend fun updateService(@Path("id") id: String, @Body service: Service): Response<Service>
} 
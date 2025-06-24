package com.isra2.desasolve2.repository

import com.isra2.desasolve2.api.ApiClient
import com.isra2.desasolve2.models.Quote
import com.isra2.desasolve2.models.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceRepository {
    private val apiService = ApiClient.apiService
    
    suspend fun getQuotes(): Result<List<Quote>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getQuotes()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener cotizaciones: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun acceptQuote(quoteId: String): Result<Quote> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.acceptQuote(quoteId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al aceptar cotización: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun rejectQuote(quoteId: String): Result<Quote> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.rejectQuote(quoteId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al rechazar cotización: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getServices(): Result<List<Service>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getServices()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener servicios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createService(service: Service): Result<Service> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createService(service)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear servicio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateService(serviceId: String, service: Service): Result<Service> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateService(serviceId, service)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar servicio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 
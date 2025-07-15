package com.isra2.desasolve2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isra2.desasolve2.models.*
import com.isra2.desasolve2.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class QuotesViewModel : ViewModel() {
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadQuotes()
    }
    
    fun loadQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = ApiClient.apiService.getQuotes()
                if (response.isSuccessful) {
                    _quotes.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar cotizaciones: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                // Fallback a datos simulados si no hay conexión
                _quotes.value = getDummyQuotes()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun acceptQuote(quoteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = ApiClient.apiService.acceptQuote(quoteId)
                if (response.isSuccessful) {
                    // Actualizar la cotización en la lista local
                    _quotes.value = _quotes.value.map { quote ->
                        if (quote.id == quoteId) {
                            response.body() ?: quote.copy(status = QuoteStatus.ACCEPTED)
                        } else {
                            quote
                        }
                    }
                } else {
                    _error.value = "Error al aceptar la cotización: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun rejectQuote(quoteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = ApiClient.apiService.rejectQuote(quoteId)
                if (response.isSuccessful) {
                    // Actualizar la cotización en la lista local
                    _quotes.value = _quotes.value.map { quote ->
                        if (quote.id == quoteId) {
                            response.body() ?: quote.copy(status = QuoteStatus.REJECTED)
                        } else {
                            quote
                        }
                    }
                } else {
                    _error.value = "Error al rechazar la cotización: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getFilteredQuotes(status: QuoteStatus): List<Quote> {
        return quotes.value.filter { it.status == status }
    }
    
    fun createQuote(
        clientName: String,
        clientAddress: String,
        clientPhone: String,
        serviceDescription: String,
        serviceType: ServiceType,
        laborCost: Double,
        materials: List<Material>,
        additionalCosts: List<AdditionalCost>,
        validUntil: LocalDate
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val totalAmount = laborCost + materials.sumOf { it.total } + additionalCosts.sumOf { it.amount }
                
                val newQuote = Quote(
                    id = "", // El backend asignará el ID
                    serviceId = "", // El backend asignará el serviceId
                    amount = totalAmount,
                    description = serviceDescription,
                    status = QuoteStatus.PENDING,
                    createdAt = LocalDateTime.now(),
                    validUntil = validUntil,
                    clientAddress = clientAddress,
                    clientName = clientName,
                    clientPhone = clientPhone,
                    materials = materials.ifEmpty { null },
                    laborCost = laborCost,
                    additionalCosts = additionalCosts.ifEmpty { null }
                )
                
                val response = ApiClient.apiService.createQuote(newQuote)
                if (response.isSuccessful) {
                    val createdQuote = response.body()
                    if (createdQuote != null) {
                        // Agregar la nueva cotización a la lista
                        _quotes.value = _quotes.value + createdQuote
                    }
                } else {
                    _error.value = "Error al crear la cotización: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun getDummyQuotes(): List<Quote> {
        return listOf(
            Quote(
                id = "1",
                serviceId = "1",
                amount = 0.0, // Sin precio
                description = "Limpieza profunda de cocina industrial",
                status = QuoteStatus.PENDING,
                createdAt = LocalDateTime.now().minusDays(1),
                validUntil = LocalDate.now().plusDays(7),
                clientAddress = "Av. Principal 123, Centro, Ciudad de México",
                clientName = "Restaurante El Buen Sabor",
                clientPhone = "555-123-4567",
                materials = null,
                laborCost = null,
                additionalCosts = null
            ),
            Quote(
                id = "2",
                serviceId = "2",
                amount = 0.0,
                description = "Desazolve de drenaje principal",
                status = QuoteStatus.PENDING,
                createdAt = LocalDateTime.now().minusDays(2),
                validUntil = LocalDate.now().plusDays(5),
                clientAddress = "Calle Comercial 456, Zona Hotelera, Cancún",
                clientName = "Hotel Plaza Mayor",
                clientPhone = "998-456-7890",
                materials = null,
                laborCost = null,
                additionalCosts = null
            ),
            Quote(
                id = "3",
                serviceId = "3",
                amount = 0.0,
                description = "Mantenimiento de campana extractora",
                status = QuoteStatus.ACCEPTED,
                createdAt = LocalDateTime.now().minusDays(5),
                validUntil = LocalDate.now().plusDays(2),
                clientAddress = "Plaza Mayor 789, Centro Histórico, Guadalajara",
                clientName = "Cafetería Central",
                clientPhone = "333-789-0123",
                materials = null,
                laborCost = null,
                additionalCosts = null
            ),
            Quote(
                id = "4",
                serviceId = "4",
                amount = 0.0,
                description = "Limpieza de trampas de grasa",
                status = QuoteStatus.REJECTED,
                createdAt = LocalDateTime.now().minusDays(3),
                validUntil = LocalDate.now().plusDays(4),
                clientAddress = "Puerto 321, Zona Portuaria, Veracruz",
                clientName = "Restaurante Mariscos",
                clientPhone = "229-321-6540",
                materials = null,
                laborCost = null,
                additionalCosts = null
            ),
            Quote(
                id = "5",
                serviceId = "5",
                amount = 0.0,
                description = "Limpieza de ductos de ventilación",
                status = QuoteStatus.PENDING,
                createdAt = LocalDateTime.now().minusHours(6),
                validUntil = LocalDate.now().plusDays(3),
                clientAddress = "Blvd. Industrial 654, Parque Industrial, Monterrey",
                clientName = "Fábrica de Alimentos",
                clientPhone = "818-654-3210",
                materials = null,
                laborCost = null,
                additionalCosts = null
            )
        )
    }
} 
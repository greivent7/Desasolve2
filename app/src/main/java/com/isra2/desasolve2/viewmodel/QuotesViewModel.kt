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
} 
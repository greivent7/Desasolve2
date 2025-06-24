package com.isra2.desasolve2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isra2.desasolve2.models.*
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
                // Simulamos una carga de datos
                kotlinx.coroutines.delay(1000)
                _quotes.value = getDummyQuotes()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
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
                // Simulamos un retraso de red
                kotlinx.coroutines.delay(500)
                
                // Actualizamos la cotización localmente
                _quotes.value = _quotes.value.map { quote ->
                    if (quote.id == quoteId) {
                        quote.copy(status = QuoteStatus.ACCEPTED)
                    } else {
                        quote
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al aceptar la solicitud"
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
                // Simulamos un retraso de red
                kotlinx.coroutines.delay(500)
                
                // Actualizamos la cotización localmente
                _quotes.value = _quotes.value.map { quote ->
                    if (quote.id == quoteId) {
                        quote.copy(status = QuoteStatus.REJECTED)
                    } else {
                        quote
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al rechazar la solicitud"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getFilteredQuotes(status: QuoteStatus): List<Quote> {
        return quotes.value.filter { it.status == status }
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
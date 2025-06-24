package com.isra2.desasolve2.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.time.LocalDateTime

// Tipos de servicio
enum class ServiceType {
    KITCHEN, DRAINAGE, MAINTENANCE, CLEANING
}

// Estados de servicio
enum class ServiceStatus {
    COMPLETED, IN_PROGRESS, PENDING, SCHEDULED, QUOTED, QUOTE_ACCEPTED, QUOTE_REJECTED
}

// Estados de cotización
enum class QuoteStatus {
    PENDING,    // Cotización pendiente de revisión
    ACCEPTED,   // Cotización aceptada
    REJECTED    // Cotización rechazada
}

// Niveles de urgencia
enum class UrgencyLevel(val displayName: String) {
    LOW("Baja"),
    NORMAL("Normal"),
    HIGH("Alta")
}

// Tipos de notificación
enum class NotificationType {
    SERVICE_COMPLETED,
    SERVICE_SCHEDULED,
    SERVICE_REMINDER,
    SERVICE_UPDATED,
    SYSTEM
}

// Filtros de notificación
enum class NotificationFilter(val displayName: String) {
    ALL("Todas"),
    UNREAD("No leídas"),
    IMPORTANT("Importantes"),
    SERVICES("Servicios")
}

// Modelo de servicio actualizado
data class Service(
    val id: String,
    val clientName: String,
    val address: String,
    val date: LocalDate,
    val time: String,
    val type: ServiceType,
    val status: ServiceStatus,
    val quote: Quote? = null,
    val notes: String? = null
)

// Modelo de asignación
data class Assignment(
    val id: String,
    val clientName: String,
    val address: String,
    val date: LocalDate,
    val time: String,
    val type: ServiceType,
    val isCompleted: Boolean,
    val estimatedDuration: Int // en minutos
)

// Modelo de trabajador
data class Worker(
    val id: String,
    val name: String,
    val role: String,
    val avatar: String? = null
)

// Modelo de notificación
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val type: NotificationType,
    val isRead: Boolean = false,
    val isImportant: Boolean = false,
    val hasAction: Boolean = false,
    val actionText: String? = null
)

// Modelo de cotización
data class Quote(
    val id: String,
    val serviceId: String,
    val amount: Double,
    val description: String,
    val status: QuoteStatus,
    val createdAt: LocalDateTime,
    val validUntil: LocalDate,
    val clientAddress: String? = null,
    val clientName: String? = null,
    val clientPhone: String? = null,
    val materials: List<Material>? = null,
    val laborCost: Double? = null,
    val additionalCosts: List<AdditionalCost>? = null
)

// Modelo para materiales
data class Material(
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double
)

// Modelo para costos adicionales
data class AdditionalCost(
    val description: String,
    val amount: Double
)

// Funciones auxiliares para colores de servicios
fun getServiceTypeColor(type: ServiceType): Color {
    return when (type) {
        ServiceType.KITCHEN -> Color(0xFFFF6B35)
        ServiceType.DRAINAGE -> Color(0xFF4ECDC4)
        ServiceType.MAINTENANCE -> Color(0xFF45B7D1)
        ServiceType.CLEANING -> Color(0xFF96CEB4)
    }
}

// Funciones auxiliares para iconos de servicios
fun getServiceIcon(type: ServiceType): ImageVector {
    return when (type) {
        ServiceType.KITCHEN -> Icons.Outlined.Restaurant
        ServiceType.DRAINAGE -> Icons.Outlined.WaterDrop
        ServiceType.MAINTENANCE -> Icons.Outlined.Build
        ServiceType.CLEANING -> Icons.Outlined.CleaningServices
    }
}

// Función para obtener el nombre del tipo de servicio
fun getServiceTypeName(type: ServiceType): String {
    return when (type) {
        ServiceType.KITCHEN -> "Cocinas"
        ServiceType.DRAINAGE -> "Desazolve"
        ServiceType.MAINTENANCE -> "Mantenimiento"
        ServiceType.CLEANING -> "Limpieza"
    }
} 
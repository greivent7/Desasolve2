package com.isra2.desasolve2.ui.screens.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.isra2.desasolve2.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }
    val notifications = remember { getDummyNotifications() }
    val filteredNotifications = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.IMPORTANT -> notifications.filter { it.isImportant }
            NotificationFilter.SERVICES -> notifications.filter { it.type in listOf(NotificationType.SERVICE_COMPLETED, NotificationType.SERVICE_SCHEDULED) }
        }
    }

    Scaffold(
        topBar = {
            NotificationsHeader(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Marcar todas como leídas */ },
                containerColor = DeepSkyBlue,
                contentColor = PureWhite,
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = "Marcar como leídas")
            }
        }
    ) { paddingValues ->
        if (filteredNotifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Resumen de notificaciones
                    NotificationsSummary(notifications = notifications)
                }
                
                items(filteredNotifications) { notification ->
                    ModernNotificationCard(
                        notification = notification,
                        onClick = { /* TODO: Abrir notificación */ }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationsHeader(
    selectedFilter: NotificationFilter,
    onFilterChanged: (NotificationFilter) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundPrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                DeepSkyBlue,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                
                IconButton(
                    onClick = { /* TODO: Configuración de notificaciones */ },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            SurfaceSecondary,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Configuración",
                        tint = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Filtros
            NotificationFilters(
                selectedFilter = selectedFilter,
                onFilterChanged = onFilterChanged
            )
        }
    }
}

@Composable
fun NotificationFilters(
    selectedFilter: NotificationFilter,
    onFilterChanged: (NotificationFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(NotificationFilter.values()) { filter ->
            FilterChip(
                filter = filter,
                isSelected = selectedFilter == filter,
                onClick = { onFilterChanged(filter) }
            )
        }
    }
}

@Composable
fun FilterChip(
    filter: NotificationFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 4.dp else 2.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (isSelected) DeepSkyBlue else SurfaceSecondary,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = filter.displayName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (isSelected) PureWhite else TextPrimary
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun NotificationsSummary(notifications: List<Notification>) {
    val unreadCount = notifications.count { !it.isRead }
    val importantCount = notifications.count { it.isImportant }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSecondary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem(
                icon = Icons.Outlined.Notifications,
                value = notifications.size.toString(),
                label = "Total",
                color = TextPrimary
            )
            
            SummaryItem(
                icon = Icons.Outlined.MarkEmailUnread,
                value = unreadCount.toString(),
                label = "No leídas",
                color = if (unreadCount > 0) StatusPending else TextSecondary
            )
            
            SummaryItem(
                icon = Icons.Outlined.PriorityHigh,
                value = importantCount.toString(),
                label = "Importantes",
                color = if (importantCount > 0) StatusInProgress else TextSecondary
            )
        }
    }
}

@Composable
fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary
            )
        )
    }
}

@Composable
fun ModernNotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) SurfaceTertiary else SurfaceSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de notificación
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        getNotificationTypeColor(notification.type).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationTypeColor(notification.type),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (notification.isImportant) {
                            Icon(
                                Icons.Outlined.PriorityHigh,
                                contentDescription = "Importante",
                                tint = StatusInProgress,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        StatusPending,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    maxLines = 3
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatNotificationTime(notification.timestamp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary
                        )
                    )
                    
                    if (notification.hasAction) {
                        TextButton(
                            onClick = { /* TODO: Acción de notificación */ },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = DeepSkyBlue
                            )
                        ) {
                            Text(
                                text = notification.actionText ?: "Ver más",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        SurfaceSecondary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.NotificationsOff,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No hay notificaciones",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Cuando tengas notificaciones nuevas, aparecerán aquí",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Funciones auxiliares
private fun getNotificationTypeColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.SERVICE_COMPLETED -> StatusCompleted
        NotificationType.SERVICE_SCHEDULED -> StatusScheduled
        NotificationType.SERVICE_REMINDER -> StatusInProgress
        NotificationType.SERVICE_UPDATED -> StatusPending
        NotificationType.SYSTEM -> TextSecondary
    }
}

private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.SERVICE_COMPLETED -> Icons.Outlined.CheckCircle
    NotificationType.SERVICE_SCHEDULED -> Icons.Outlined.Schedule
    NotificationType.SERVICE_REMINDER -> Icons.Outlined.Notifications
    NotificationType.SERVICE_UPDATED -> Icons.Outlined.Update
    NotificationType.SYSTEM -> Icons.Outlined.Info
}

private fun formatNotificationTime(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val diff = java.time.Duration.between(timestamp, now)
    
    return when {
        diff.toMinutes() < 1 -> "Ahora"
        diff.toMinutes() < 60 -> "Hace ${diff.toMinutes()} min"
        diff.toHours() < 24 -> "Hace ${diff.toHours()} h"
        else -> timestamp.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
    }
}

// Modelos de datos
enum class NotificationFilter(val displayName: String) {
    ALL("Todas"),
    UNREAD("No leídas"),
    IMPORTANT("Importantes"),
    SERVICES("Servicios")
}

enum class NotificationType {
    SERVICE_COMPLETED,
    SERVICE_SCHEDULED,
    SERVICE_REMINDER,
    SERVICE_UPDATED,
    SYSTEM
}

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

private fun getDummyNotifications(): List<Notification> {
    val now = LocalDateTime.now()
    return listOf(
        Notification(
            id = "1",
            title = "Servicio Completado",
            message = "La limpieza en Restaurante El Buen Sabor ha sido completada exitosamente. El cliente ha calificado el servicio con 5 estrellas.",
            timestamp = now.minusMinutes(30),
            type = NotificationType.SERVICE_COMPLETED,
            isRead = false,
            isImportant = true,
            hasAction = true,
            actionText = "Ver detalles"
        ),
        Notification(
            id = "2",
            title = "Nuevo Servicio Programado",
            message = "Se ha programado una limpieza para Cafetería Central mañana a las 14:00. Duración estimada: 2 horas.",
            timestamp = now.minusHours(2),
            type = NotificationType.SERVICE_SCHEDULED,
            isRead = false,
            hasAction = true,
            actionText = "Ver agenda"
        ),
        Notification(
            id = "3",
            title = "Recordatorio de Servicio",
            message = "Tienes un servicio programado en Hotel Plaza Mayor en 1 hora. No olvides llevar el equipo de desazolve.",
            timestamp = now.minusHours(4),
            type = NotificationType.SERVICE_REMINDER,
            isRead = true,
            isImportant = true
        ),
        Notification(
            id = "4",
            title = "Servicio Reprogramado",
            message = "El servicio en Restaurante Mariscos ha sido reprogramado para el viernes a las 16:00 debido a mantenimiento del edificio.",
            timestamp = now.minusHours(6),
            type = NotificationType.SERVICE_UPDATED,
            isRead = true,
            hasAction = true,
            actionText = "Confirmar"
        ),
        Notification(
            id = "5",
            title = "Mantenimiento del Sistema",
            message = "El sistema estará en mantenimiento esta noche de 2:00 a 4:00 AM. Los servicios programados no se verán afectados.",
            timestamp = now.minusHours(8),
            type = NotificationType.SYSTEM,
            isRead = true
        )
    )
} 
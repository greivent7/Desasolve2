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
    val notifications = remember { listOf<Notification>() } // TODO: Reemplazar por datos reales del ViewModel
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
            ModernNotificationsHeader(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Marcar todas como leídas */ },
                containerColor = InteractivePrimary,
                contentColor = PureWhite,
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = "Marcar como leídas")
                    Text(
                        text = "Leídas",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ModernNotificationsHeader(
    selectedFilter: NotificationFilter,
    onFilterChanged: (NotificationFilter) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfacePrimary,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Mantente informado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        )
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { /* TODO: Configuración de notificaciones */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                InteractivePrimary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Configuración",
                            tint = InteractivePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filtros mejorados
            ModernNotificationFilters(
                selectedFilter = selectedFilter,
                onFilterChanged = onFilterChanged
            )
        }
    }
}

@Composable
fun ModernNotificationFilters(
    selectedFilter: NotificationFilter,
    onFilterChanged: (NotificationFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(NotificationFilter.values()) { filter ->
            ModernFilterChip(
                filter = filter,
                isSelected = selectedFilter == filter,
                onClick = { onFilterChanged(filter) }
            )
        }
    }
}

@Composable
fun ModernFilterChip(
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
        color = if (isSelected) InteractivePrimary else SurfacePrimary,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = when (filter) {
                NotificationFilter.ALL -> "Todas"
                NotificationFilter.UNREAD -> "No leídas"
                NotificationFilter.IMPORTANT -> "Importantes"
                NotificationFilter.SERVICES -> "Servicios"
            },
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
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
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NotificationSummaryItem(
                icon = Icons.Outlined.Notifications,
                count = notifications.size,
                label = "Total",
                color = InteractivePrimary
            )
            NotificationSummaryItem(
                icon = Icons.Outlined.MarkEmailUnread,
                count = unreadCount,
                label = "No leídas",
                color = StatusPending
            )
            NotificationSummaryItem(
                icon = Icons.Outlined.PriorityHigh,
                count = importantCount,
                label = "Importantes",
                color = StatusInProgress
            )
        }
    }
}

@Composable
fun NotificationSummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                )
            )
        }
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
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) SurfacePrimary else SurfaceTertiary
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de notificación
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        getNotificationIconColor(notification.type).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationIconColor(notification.type),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    maxLines = 2
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatNotificationTime(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextTertiary
                        )
                    )
                    
                    if (notification.isImportant) {
                        Icon(
                            Icons.Outlined.PriorityHigh,
                            contentDescription = "Importante",
                            tint = StatusInProgress,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    InteractivePrimary.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.NotificationsOff,
                contentDescription = null,
                tint = InteractivePrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No hay notificaciones",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Cuando recibas notificaciones, aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

// Funciones helper
fun getNotificationIcon(type: NotificationType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        NotificationType.SERVICE_COMPLETED -> Icons.Outlined.CheckCircle
        NotificationType.SERVICE_SCHEDULED -> Icons.Outlined.Schedule
        NotificationType.PAYMENT_RECEIVED -> Icons.Outlined.Payment
        NotificationType.SYSTEM_UPDATE -> Icons.Outlined.SystemUpdate
        NotificationType.URGENT_MESSAGE -> Icons.Outlined.PriorityHigh
    }
}

fun getNotificationIconColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.SERVICE_COMPLETED -> StatusCompleted
        NotificationType.SERVICE_SCHEDULED -> StatusScheduled
        NotificationType.PAYMENT_RECEIVED -> StatusCompleted
        NotificationType.SYSTEM_UPDATE -> InteractivePrimary
        NotificationType.URGENT_MESSAGE -> StatusPending
    }
}

fun formatNotificationTime(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val diff = java.time.Duration.between(timestamp, now)
    
    return when {
        diff.toMinutes() < 1 -> "Ahora"
        diff.toMinutes() < 60 -> "Hace ${diff.toMinutes()} min"
        diff.toHours() < 24 -> "Hace ${diff.toHours()} h"
        else -> timestamp.format(DateTimeFormatter.ofPattern("dd/MM"))
    }
}

// Data classes y enums
enum class NotificationFilter {
    ALL, UNREAD, IMPORTANT, SERVICES
}

enum class NotificationType {
    SERVICE_COMPLETED, SERVICE_SCHEDULED, PAYMENT_RECEIVED, SYSTEM_UPDATE, URGENT_MESSAGE
}

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val isImportant: Boolean = false
) 
package com.isra2.desasolve2.ui.screens.servicedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.isra2.desasolve2.navigation.Screen
import com.isra2.desasolve2.models.ServiceStatus
import com.isra2.desasolve2.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(
    serviceId: String,
    navController: NavController
) {
    var currentStatus by remember { mutableStateOf(ServiceStatus.IN_PROGRESS) }

    Scaffold(
        topBar = {
            ModernTopBar(
                title = "Detalles del Servicio",
                subtitle = "Gestiona el estado",
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.BeforeAfterPhotos.createRoute(serviceId))
                },
                containerColor = InteractivePrimary,
                contentColor = PureWhite,
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Tomar fotos")
                    Text(
                        text = "Fotos",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información del servicio
            ServiceInfoCard(serviceId = serviceId)
            
            // Estado del servicio
            ServiceStatusCard(
                currentStatus = currentStatus,
                onStatusChange = { currentStatus = it }
            )
            
            // Información del cliente
            ClientInfoCard()
            
            // Acciones rápidas
            QuickActionsCard(
                serviceId = serviceId,
                navController = navController
            )
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ModernTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfacePrimary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        InteractivePrimary.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = InteractivePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun ServiceInfoCard(serviceId: String) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        InteractivePrimary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.CleaningServices,
                    contentDescription = null,
                    tint = InteractivePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Servicio #$serviceId",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Desazolve y limpieza",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun ServiceStatusCard(
    currentStatus: ServiceStatus,
    onStatusChange: (ServiceStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Estado del Servicio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModernStatusButton(
                    text = "Completado",
                    icon = Icons.Outlined.CheckCircle,
                    selected = currentStatus == ServiceStatus.COMPLETED,
                    color = StatusCompleted
                ) {
                    onStatusChange(ServiceStatus.COMPLETED)
                }
                
                ModernStatusButton(
                    text = "En Proceso",
                    icon = Icons.Outlined.Pending,
                    selected = currentStatus == ServiceStatus.IN_PROGRESS,
                    color = StatusInProgress
                ) {
                    onStatusChange(ServiceStatus.IN_PROGRESS)
                }
                
                ModernStatusButton(
                    text = "Pendiente",
                    icon = Icons.Outlined.Schedule,
                    selected = currentStatus == ServiceStatus.PENDING,
                    color = StatusPending
                ) {
                    onStatusChange(ServiceStatus.PENDING)
                }
            }
        }
    }
}

@Composable
fun ModernStatusButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .background(
                    if (selected) color else color.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = if (selected) PureWhite else color,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) color else TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ClientInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Información del Cliente",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            ModernInfoRow(
                icon = Icons.Outlined.Business,
                label = "Negocio",
                value = "Restaurante El Sabor"
            )
            
            ModernInfoRow(
                icon = Icons.Outlined.LocationOn,
                label = "Dirección",
                value = "Av. Principal 123, Col. Centro"
            )
            
            ModernInfoRow(
                icon = Icons.Outlined.Phone,
                label = "Contacto",
                value = "555-0123"
            )
            
            ModernInfoRow(
                icon = Icons.Outlined.Schedule,
                label = "Hora Programada",
                value = "09:00 AM"
            )
        }
    }
}

@Composable
fun ModernInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    InteractivePrimary.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = InteractivePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
        }
    }
}

@Composable
fun QuickActionsCard(
    serviceId: String,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernActionButton(
                    text = "Fotos",
                    icon = Icons.Outlined.PhotoCamera,
                    onClick = {
                        navController.navigate(Screen.BeforeAfterPhotos.createRoute(serviceId))
                    },
                    modifier = Modifier.weight(1f)
                )
                
                ModernActionButton(
                    text = "Ubicación",
                    icon = Icons.Outlined.Map,
                    onClick = { /* Handle location */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ModernActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = InteractivePrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
} 
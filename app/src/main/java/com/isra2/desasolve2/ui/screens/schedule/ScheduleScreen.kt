package com.isra2.desasolve2.ui.screens.schedule

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.isra2.desasolve2.R
import com.isra2.desasolve2.navigation.Screen
import com.isra2.desasolve2.ui.theme.*
import com.isra2.desasolve2.models.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    val services = remember { listOf<Service>() } // TODO: Reemplazar por datos reales del ViewModel
    val haptic = LocalHapticFeedback.current
    
    // Estado para filtros - ahora persistente
    var selectedServiceType by remember { mutableStateOf<ServiceType?>(null) }
    var showCompletedServices by remember { mutableStateOf(true) }
    var showPendingServices by remember { mutableStateOf(true) }
    var showInProgressServices by remember { mutableStateOf(true) }
    var showScheduledServices by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            ModernHeader(
                onFilterApplied = { serviceType, completed, pending, inProgress, scheduled ->
                    selectedServiceType = serviceType
                    showCompletedServices = completed
                    showPendingServices = pending
                    showInProgressServices = inProgress
                    showScheduledServices = scheduled
                },
                // Pasar el estado actual de los filtros
                currentFilters = FilterState(
                    selectedServiceType = selectedServiceType,
                    showCompletedServices = showCompletedServices,
                    showPendingServices = showPendingServices,
                    showInProgressServices = showInProgressServices,
                    showScheduledServices = showScheduledServices
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ServiceRequest.route) },
                containerColor = DeepSkyBlue,
                contentColor = PureWhite,
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Agregar servicio",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Calendario con desplazamiento infinito
                InfiniteCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { 
                        selectedDate = it
                    },
                    onMonthChanged = { 
                        currentMonth = it
                    }
                )
            }
            
            item {
                // Información del día seleccionado
                SelectedDateInfo(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }
            
            // Lista de servicios del día con filtros aplicados
            val dayServices = services.filter { service ->
                service.date == selectedDate &&
                (selectedServiceType == null || service.type == selectedServiceType) &&
                ((service.status == ServiceStatus.COMPLETED && showCompletedServices) ||
                 (service.status == ServiceStatus.PENDING && showPendingServices) ||
                 (service.status == ServiceStatus.IN_PROGRESS && showInProgressServices) ||
                 (service.status == ServiceStatus.SCHEDULED && showScheduledServices))
            }
            
            if (dayServices.isNotEmpty()) {
                items(dayServices) { service ->
                    ServiceCard(
                        service = service,
                        onClick = {
                            navController.navigate(Screen.ServiceDetails.createRoute(service.id))
                        }
                    )
                }
            } else {
                item {
                    EmptyDayCard()
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Data class para mantener el estado de los filtros
data class FilterState(
    val selectedServiceType: ServiceType?,
    val showCompletedServices: Boolean,
    val showPendingServices: Boolean,
    val showInProgressServices: Boolean,
    val showScheduledServices: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHeader(onFilterApplied: (ServiceType?, Boolean, Boolean, Boolean, Boolean) -> Unit, currentFilters: FilterState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceSecondary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Título
            Column {
                Text(
                    text = "Calendario",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Gestiona tus servicios",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
            }
            
            // Botón de filtro simplificado
            IconButton(
                onClick = { /* Filtros en desarrollo */ }
            ) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = "Filtros",
                    tint = InteractivePrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersDialog(onDismiss: () -> Unit, onFilterApplied: (ServiceType?, Boolean, Boolean, Boolean, Boolean) -> Unit, currentFilters: FilterState) {
    var selectedServiceType by remember { mutableStateOf<ServiceType?>(currentFilters.selectedServiceType) }
    var selectedStatus by remember { mutableStateOf<ServiceStatus?>(null) }
    var showCompletedServices by remember { mutableStateOf(currentFilters.showCompletedServices) }
    var showPendingServices by remember { mutableStateOf(currentFilters.showPendingServices) }
    var showInProgressServices by remember { mutableStateOf(currentFilters.showInProgressServices) }
    var showScheduledServices by remember { mutableStateOf(currentFilters.showScheduledServices) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filtros Avanzados",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filtro por tipo de servicio
                Text(
                    text = "Tipo de Servicio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ServiceType.values()) { serviceType ->
                        FilterChip(
                            selected = selectedServiceType == serviceType,
                            onClick = { 
                                selectedServiceType = if (selectedServiceType == serviceType) null else serviceType 
                            },
                            label = {
                                Text(getServiceTypeName(serviceType))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepSkyBlue,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                }
                
                Divider(color = LightGray)
                
                // Filtro por estado
                Text(
                    text = "Estado del Servicio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completados",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary
                            )
                        )
                        Switch(
                            checked = showCompletedServices,
                            onCheckedChange = { showCompletedServices = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = DeepSkyBlue
                            )
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "En Progreso",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary
                            )
                        )
                        Switch(
                            checked = showInProgressServices,
                            onCheckedChange = { showInProgressServices = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = DeepSkyBlue
                            )
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pendientes",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary
                            )
                        )
                        Switch(
                            checked = showPendingServices,
                            onCheckedChange = { showPendingServices = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = DeepSkyBlue
                            )
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Programados",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary
                            )
                        )
                        Switch(
                            checked = showScheduledServices,
                            onCheckedChange = { showScheduledServices = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = DeepSkyBlue
                            )
                        )
                    }
                }
                
                Divider(color = LightGray)
                
                // Estadísticas rápidas
                Text(
                    text = "Estadísticas del Día",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                val todayServices = listOf<Service>() // TODO: Reemplazar por datos reales del ViewModel
                val totalServices = todayServices.size
                val completedServices = todayServices.count { it.status == ServiceStatus.COMPLETED }
                val pendingServices = todayServices.count { it.status == ServiceStatus.PENDING }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        title = "Total",
                        value = totalServices.toString(),
                        color = DeepSkyBlue
                    )
                    StatCard(
                        title = "Completados",
                        value = completedServices.toString(),
                        color = StatusCompleted
                    )
                    StatCard(
                        title = "Pendientes",
                        value = pendingServices.toString(),
                        color = StatusPending
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFilterApplied(selectedServiceType, showCompletedServices, showPendingServices, showInProgressServices, showScheduledServices)
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Text("Aplicar Filtros")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextSecondary
                )
            ) {
                Text("Cancelar")
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfiniteCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit
) {
    val services = remember { listOf<Service>() } // TODO: Reemplazar por datos reales del ViewModel
    val servicesByDate = remember(services) {
        services.groupBy { it.date }
    }
    
    // Simplificar las fechas para evitar problemas de memoria
    val allDates = remember {
        val today = LocalDate.now()
        val startDate = today.minusDays(30) // Reducir de 365 a 30 días
        val endDate = today.plusDays(30)    // Reducir de 365 a 30 días
        (0..(endDate.toEpochDay() - startDate.toEpochDay()).toInt()).map { 
            startDate.plusDays(it.toLong()) 
        }
    }
    
    val itemSpacing = 12.dp
    val horizontalPadding = 24.dp
    
    // Estado para controlar el scroll del LazyRow
    val listState = rememberLazyListState()
    
    // Hacer scroll automático cuando se selecciona una fecha
    LaunchedEffect(selectedDate) {
        val targetIndex = allDates.indexOf(selectedDate)
        if (targetIndex != -1) {
            listState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = 0
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfacePrimary
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header del calendario mejorado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es")))
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Selecciona una fecha",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        )
                    )
                }
                
                TodayButton(
                    onClick = {
                        val today = LocalDate.now()
                        onDateSelected(today)
                        // El scroll automático se activará por el LaunchedEffect
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Calendario con mejor espaciado y scroll controlado
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(allDates) { date ->
                    CalendarDay3D(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == LocalDate.now(),
                        hasServices = servicesByDate[date]?.isNotEmpty() == true,
                        scale = if (date == selectedDate) 1.1f else 1.0f,
                        alpha = 1f,
                        onClick = {
                            onDateSelected(date)
                        },
                        selectionColor = DeepSkyBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador de servicios
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CalendarHasService, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Días con servicios programados",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
    
    // Notificar cambio de mes
    LaunchedEffect(selectedDate) {
        onMonthChanged(selectedDate)
    }
}

@Composable
fun TodayButton(onClick: () -> Unit) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = DeepSkyBlue
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Today,
                contentDescription = "Hoy",
                tint = PureWhite,
                modifier = Modifier.size(18.dp)
            )
            
            Text(
                text = "Hoy",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun CalendarDay3D(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasServices: Boolean,
    scale: Float,
    alpha: Float,
    onClick: () -> Unit,
    selectionColor: Color = DeepSkyBlue
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale * 1.1f else scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale"
    )
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else if (isToday) 8.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "elevation"
    )
    
    val borderColor = when {
        isSelected -> selectionColor
        isToday -> CalendarSelected
        else -> Color.Transparent
    }
    
    val backgroundColor = when {
        isSelected -> selectionColor
        isToday -> CalendarSelected.copy(alpha = 0.15f)
        else -> SurfaceSecondary
    }
    
    val textColor = when {
        isSelected -> PureWhite
        isToday -> CalendarSelected
        else -> TextPrimary
    }
    
    val secondaryTextColor = when {
        isSelected -> PureWhite.copy(alpha = 0.8f)
        isToday -> CalendarSelected.copy(alpha = 0.7f)
        else -> TextSecondary
    }
    
    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                this.alpha = alpha
            }
            .size(72.dp, 88.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            border = if (isSelected || isToday) BorderStroke(2.dp, borderColor) else null,
            elevation = CardDefaults.cardElevation(
                defaultElevation = animatedElevation
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Día de la semana con mejor tipografía
                Text(
                    text = getWeekdayShort(date),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = secondaryTextColor,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                
                // Día numérico con mejor jerarquía
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 24.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Indicador de servicios mejorado
                if (hasServices) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        if (isSelected) PureWhite.copy(alpha = 0.8f) else CalendarHasService,
                                        CircleShape
                                    )
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Función auxiliar para obtener el día de la semana abreviado mejorado
private fun getWeekdayShort(date: LocalDate): String {
    return when (date.dayOfWeek.value) {
        1 -> "LUN"
        2 -> "MAR"
        3 -> "MIÉ"
        4 -> "JUE"
        5 -> "VIE"
        6 -> "SÁB"
        7 -> "DOM"
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedDateInfo(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Mover el estado del DatePicker fuera del bloque if para que esté en el contexto composable
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfacePrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Día de la semana
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE", Locale("es")))
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Fecha completa con mejor formato
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es")))
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Indicador de día especial
                if (selectedDate == LocalDate.now()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(DeepSkyBlue, CircleShape)
                        )
                        Text(
                            text = "Hoy",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepSkyBlue,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
            
            // Botón de calendario mejorado
            Card(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = DeepSkyBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    
    // DatePicker real
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Text(
                    text = "Seleccionar Fecha",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = SurfacePrimary,
                        titleContentColor = TextPrimary,
                        headlineContentColor = TextPrimary,
                        weekdayContentColor = TextSecondary,
                        subheadContentColor = TextSecondary,
                        yearContentColor = TextSecondary,
                        currentYearContentColor = DeepSkyBlue,
                        selectedYearContentColor = PureWhite,
                        selectedYearContainerColor = DeepSkyBlue,
                        dayContentColor = TextPrimary,
                        selectedDayContentColor = PureWhite,
                        selectedDayContainerColor = DeepSkyBlue,
                        todayContentColor = DeepSkyBlue,
                        todayDateBorderColor = DeepSkyBlue
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedLocalDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(selectedLocalDate)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = DeepSkyBlue
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = SurfacePrimary,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ServiceCard(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfacePrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del servicio
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        getServiceTypeColor(service.type).copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getServiceIcon(service.type),
                    contentDescription = null,
                    tint = getServiceTypeColor(service.type),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = service.clientName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                Text(
                    text = service.address,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    maxLines = 2
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.time,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary
                        )
                    )
                    
                    ServiceStatusChip(status = service.status)
                }
            }
            
            // Flecha
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun ServiceStatusChip(status: ServiceStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        ServiceStatus.COMPLETED -> Triple(StatusCompleted, TextPrimary, "Completado")
        ServiceStatus.IN_PROGRESS -> Triple(StatusInProgress, TextPrimary, "En progreso")
        ServiceStatus.PENDING -> Triple(StatusPending, PureWhite, "Pendiente")
        ServiceStatus.SCHEDULED -> Triple(StatusScheduled, PureWhite, "Programado")
        ServiceStatus.QUOTED -> Triple(Color(0xFF64B5F6), PureWhite, "Cotizado")
        ServiceStatus.QUOTE_ACCEPTED -> Triple(Color(0xFF81C784), TextPrimary, "Cotización Aceptada")
        ServiceStatus.QUOTE_REJECTED -> Triple(Color(0xFFE57373), PureWhite, "Cotización Rechazada")
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = textColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Outlined.EventAvailable,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "Sin servicios programados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Este día no tienes servicios asignados",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextTertiary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
} 
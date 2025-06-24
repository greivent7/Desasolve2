package com.isra2.desasolve2.ui.screens.schedule

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
    val services = remember { getDummyServices() }
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
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    },
                    onMonthChanged = { 
                        currentMonth = it
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    }
                )
            }
            
            item {
                // Información del día seleccionado
                SelectedDateInfo(selectedDate = selectedDate)
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
    var showFilterDialog by remember { mutableStateOf(false) }
    
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
            
            // Botón de filtros avanzados
            IconButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        DeepSkyBlue.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = "Filtros avanzados",
                    tint = DeepSkyBlue
                )
            }
        }
    }
    
    // Diálogo de filtros avanzados
    if (showFilterDialog) {
        AdvancedFiltersDialog(
            onDismiss = { showFilterDialog = false },
            onFilterApplied = onFilterApplied,
            currentFilters = currentFilters
        )
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
                
                val todayServices = getDummyServices().filter { it.date == LocalDate.now() }
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
    val listState = rememberLazyListState()
    val services = remember { getDummyServices() }
    val servicesByDate = remember(services) {
        services.groupBy { it.date }
    }
    var shouldScrollToSelected by remember { mutableStateOf(false) }
    val allDates = remember {
        val today = LocalDate.now()
        val startDate = today.minusDays(365)
        val endDate = today.plusDays(365)
        (0..(endDate.toEpochDay() - startDate.toEpochDay()).toInt()).map { 
            startDate.plusDays(it.toLong()) 
        }
    }
    val itemWidth = 64.dp
    val itemSpacing = 8.dp
    val visibleItems = 5 // Siempre 5 días
    val horizontalPadding = 32.dp

    // Centrar el seleccionado
    LaunchedEffect(selectedDate, shouldScrollToSelected) {
        if (shouldScrollToSelected) {
            val selectedIndex = allDates.indexOf(selectedDate)
            if (selectedIndex != -1) {
                val centerIndex = 2 // Siempre el 3° de 5
                val targetIndex = (selectedIndex - centerIndex).coerceAtLeast(0)
                listState.animateScrollToItem(targetIndex)
            }
            shouldScrollToSelected = false
        }
    }

    val currentMonth = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty()) {
                val centerIndex = visibleItemsInfo[visibleItemsInfo.size / 2].index
                if (centerIndex < allDates.size) {
                    allDates[centerIndex]
                } else {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        }
    }
    LaunchedEffect(currentMonth.value) {
        onMonthChanged(currentMonth.value)
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceSecondary, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        Text(
            text = currentMonth.value.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es")))
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
                modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
            TodayButton(
                onClick = {
                    val today = LocalDate.now()
                    onDateSelected(today)
                    shouldScrollToSelected = true
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(0.dp)
            ) {
                itemsIndexed(allDates) { index: Int, date: LocalDate ->
                    // Calcular la posición relativa al centro
                    val selectedIndex = allDates.indexOf(selectedDate)
                    val centerIndex = 2 // Siempre el 3° de 5
                    val distanceFromCenter = kotlin.math.abs(index - (selectedIndex - centerIndex + centerIndex))
                    val scale = when (distanceFromCenter) {
                        0 -> 1.15f // centro
                        1 -> 1.0f
                        2 -> 0.92f
                        else -> 0.85f
                    }
                    val alpha = 1f // Siempre claro
                    CalendarDay3D(
                    date = date,
                    isSelected = date == selectedDate,
                    isToday = date == LocalDate.now(),
                    hasServices = servicesByDate[date]?.isNotEmpty() == true,
                        scale = scale,
                        alpha = alpha,
                        onClick = {
                            onDateSelected(date)
                            shouldScrollToSelected = true
                        },
                        selectionColor = DeepSkyBlue
                    )
                }
            }
        }
    }
}

@Composable
fun TodayButton(onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    
    Surface(
        onClick = {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        color = DeepSkyBlue,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Today,
                contentDescription = "Hoy",
                tint = PureWhite,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "Hoy",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
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
        targetValue = if (isPressed) scale * 1.25f else scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale"
    )
    val borderColor = when {
        isSelected -> selectionColor
        isToday -> CalendarSelected
        else -> Color.Transparent
    }
    val backgroundColor = when {
        isSelected -> selectionColor.copy(alpha = 0.18f)
        isToday -> CalendarSelected.copy(alpha = 0.10f)
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                this.alpha = alpha
            }
            .size(64.dp, 80.dp)
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
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = backgroundColor,
            border = if (isSelected || isToday) BorderStroke(2.dp, borderColor) else null,
            shadowElevation = if (isSelected || isToday) 8.dp else 2.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Día de la semana (arriba)
            Text(
                text = getWeekdayShort(date),
                style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureWhite else TextSecondary,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
                // Día numérico (abajo)
            Text(
                text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureWhite else TextPrimary,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            // Indicador de servicios
            if (hasServices) {
                Box(
                    modifier = Modifier
                            .size(6.dp)
                            .background(CalendarHasService, CircleShape)
                    )
                }
            }
        }
    }
}

// Función auxiliar para obtener el día de la semana abreviado
private fun getWeekdayShort(date: LocalDate): String {
    return when (date.dayOfWeek.value) {
        1 -> "L"
        2 -> "M"
        3 -> "M"
        4 -> "J"
        5 -> "V"
        6 -> "S"
        7 -> "D"
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedDateInfo(selectedDate: LocalDate) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es")))
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
            // Solo botón de calendario
            IconButton(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        DeepSkyBlue,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Outlined.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = PureWhite
                )
            }
        }
    }
    
    // DatePicker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("OK", color = DeepSkyBlue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                ),
                colors = DatePickerDefaults.colors(
                    containerColor = PureWhite,
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
                ),
                showModeToggle = false
            )
        }
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

// Funciones auxiliares
private fun getDummyServices(): List<Service> {
    val today = LocalDate.now()
    return listOf(
        Service(
            id = "1",
            clientName = "Restaurante El Buen Sabor",
            address = "Av. Principal 123, Centro",
            date = today,
            time = "09:00",
            type = ServiceType.KITCHEN,
            status = ServiceStatus.SCHEDULED
        ),
        Service(
            id = "2",
            clientName = "Hotel Plaza Mayor",
            address = "Calle Comercial 456, Zona Hotelera",
            date = today,
            time = "14:30",
            type = ServiceType.DRAINAGE,
            status = ServiceStatus.PENDING
        ),
        Service(
            id = "3",
            clientName = "Cafetería Central",
            address = "Plaza Mayor 789, Centro Histórico",
            date = today.plusDays(1),
            time = "10:00",
            type = ServiceType.CLEANING,
            status = ServiceStatus.SCHEDULED
        ),
        Service(
            id = "4",
            clientName = "Restaurante Mariscos",
            address = "Puerto 321, Zona Portuaria",
            date = today.plusDays(2),
            time = "16:00",
            type = ServiceType.MAINTENANCE,
            status = ServiceStatus.SCHEDULED
        )
    )
} 
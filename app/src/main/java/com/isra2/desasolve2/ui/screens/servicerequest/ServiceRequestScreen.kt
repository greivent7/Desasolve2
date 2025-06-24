package com.isra2.desasolve2.ui.screens.servicerequest

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.isra2.desasolve2.models.ServiceType
import com.isra2.desasolve2.models.UrgencyLevel
import com.isra2.desasolve2.models.QuoteStatus
import com.isra2.desasolve2.models.Quote
import com.isra2.desasolve2.models.Material
import com.isra2.desasolve2.models.AdditionalCost
import com.isra2.desasolve2.ui.theme.*
import com.isra2.desasolve2.viewmodel.QuotesViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestScreen(
    navController: NavController,
    viewModel: QuotesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(QuoteStatus.PENDING) }
    val quotes by viewModel.quotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            ServiceQuotesHeader()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(paddingValues)
        ) {
            // Filtros de estado
            QuoteStatusFilters(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            
            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = DeepSkyBlue
                        )
                    }
                    error != null -> {
                        ErrorMessage(
                            error = error!!,
                            onRetry = { viewModel.loadQuotes() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    quotes.isEmpty() -> {
                        EmptyQuotesList(
                            status = selectedFilter,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            items(viewModel.getFilteredQuotes(selectedFilter)) { quote ->
                                SimpleQuoteCard(
                                    quote = quote,
                                    onAcceptClick = { viewModel.acceptQuote(quote.id) },
                                    onRejectClick = { viewModel.rejectQuote(quote.id) }
                                )
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceQuotesHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        Icons.Outlined.RequestQuote,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = "Solicitudes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun QuoteStatusFilters(
    selectedFilter: QuoteStatus,
    onFilterSelected: (QuoteStatus) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = QuoteStatus.values().indexOf(selectedFilter),
        containerColor = SurfaceSecondary,
        contentColor = DeepSkyBlue,
        edgePadding = 16.dp
    ) {
        QuoteStatus.values().forEach { status ->
            Tab(
                selected = status == selectedFilter,
                onClick = { onFilterSelected(status) },
                text = {
                    Text(
                        text = when(status) {
                            QuoteStatus.PENDING -> "Pendientes"
                            QuoteStatus.ACCEPTED -> "Aceptadas"
                            QuoteStatus.REJECTED -> "Rechazadas"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (status == selectedFilter) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                selectedContentColor = DeepSkyBlue,
                unselectedContentColor = TextSecondary
            )
        }
    }
}

@Composable
fun SimpleQuoteCard(
    quote: Quote,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfacePrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado con fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quote.createdAt.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary
                    )
                )
                
                // Estado de la solicitud
                StatusChip(status = quote.status)
            }
            
            // Información del cliente
            quote.clientName?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            
            // Descripción del servicio
            Text(
                text = quote.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
            
            // Dirección del cliente
            quote.clientAddress?.let { address ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openMaps(context, address)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = DeepSkyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(
                        Icons.Outlined.OpenInNew,
                        contentDescription = "Abrir en mapas",
                        tint = DeepSkyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Teléfono del cliente
            quote.clientPhone?.let { phone ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = DeepSkyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(
                        Icons.Outlined.OpenInNew,
                        contentDescription = "Llamar",
                        tint = DeepSkyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Botones de acción (solo para solicitudes pendientes)
            if (quote.status == QuoteStatus.PENDING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Rechazar
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StatusPending
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = StatusPending
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rechazar")
                    }
                    
                    // Botón Aceptar
                    Button(
                        onClick = onAcceptClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepSkyBlue,
                            contentColor = PureWhite
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: QuoteStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        QuoteStatus.PENDING -> Triple(Color(0xFFFFB74D), TextPrimary, "Pendiente")
        QuoteStatus.ACCEPTED -> Triple(Color(0xFF81C784), TextPrimary, "Aceptada")
        QuoteStatus.REJECTED -> Triple(Color(0xFFE57373), PureWhite, "Rechazada")
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
fun ErrorMessage(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Outlined.Error,
            contentDescription = null,
            tint = StatusPending,
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepSkyBlue,
                contentColor = PureWhite
            )
        ) {
            Icon(
                Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
fun EmptyQuotesList(
    status: QuoteStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Outlined.RequestQuote,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = when(status) {
                QuoteStatus.PENDING -> "No hay solicitudes pendientes"
                QuoteStatus.ACCEPTED -> "No hay solicitudes aceptadas"
                QuoteStatus.REJECTED -> "No hay solicitudes rechazadas"
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = when(status) {
                QuoteStatus.PENDING -> "Las solicitudes pendientes aparecerán aquí"
                QuoteStatus.ACCEPTED -> "Las solicitudes aceptadas aparecerán aquí"
                QuoteStatus.REJECTED -> "Las solicitudes rechazadas aparecerán aquí"
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

private fun openMaps(context: android.content.Context, address: String) {
    try {
        // Intentar abrir Google Maps
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Si Google Maps no está disponible, intentar con Waze
            val wazeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("waze://?q=${Uri.encode(address)}&navigate=yes"))
            
            if (wazeIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(wazeIntent)
            } else {
                // Si ninguna app está disponible, abrir en el navegador
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/${Uri.encode(address)}"))
                context.startActivity(browserIntent)
            }
        }
    } catch (e: Exception) {
        // Fallback: abrir en el navegador
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/${Uri.encode(address)}"))
        context.startActivity(browserIntent)
    }
} 
package com.isra2.desasolve2.ui.screens.servicerequest

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isra2.desasolve2.models.ServiceType
import com.isra2.desasolve2.viewmodel.QuotesViewModel
import com.isra2.desasolve2.models.QuoteStatus
import com.isra2.desasolve2.models.Material
import com.isra2.desasolve2.models.AdditionalCost
import com.isra2.desasolve2.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuoteScreen(
    navController: NavController,
    viewModel: QuotesViewModel = viewModel()
) {
    var clientName by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var serviceDescription by remember { mutableStateOf("") }
    var selectedServiceType by remember { mutableStateOf<ServiceType?>(null) }
    var laborCost by remember { mutableStateOf("") }
    var materials by remember { mutableStateOf<List<Material>>(emptyList()) }
    var additionalCosts by remember { mutableStateOf<List<AdditionalCost>>(emptyList()) }
    var validUntil by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    
    var showServiceTypeDialog by remember { mutableStateOf(false) }
    var showMaterialsDialog by remember { mutableStateOf(false) }
    var showAdditionalCostsDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            CreateQuoteHeader(
                onBackClick = { navController.navigateUp() },
                onSaveClick = {
                    // Validar que los campos requeridos estén completos
                    if (clientName.isNotEmpty() && selectedServiceType != null && serviceDescription.isNotEmpty()) {
                        val laborCostValue = laborCost.toDoubleOrNull() ?: 0.0
                        viewModel.createQuote(
                            clientName = clientName,
                            clientAddress = clientAddress,
                            clientPhone = clientPhone,
                            serviceDescription = serviceDescription,
                            serviceType = selectedServiceType!!,
                            laborCost = laborCostValue,
                            materials = materials,
                            additionalCosts = additionalCosts,
                            validUntil = validUntil
                        )
                        navController.navigateUp()
                    }
                },
                isLoading = isLoading
            )
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
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Información del cliente
            item {
                ClientInformationSection(
                    clientName = clientName,
                    onClientNameChange = { clientName = it },
                    clientAddress = clientAddress,
                    onClientAddressChange = { clientAddress = it },
                    clientPhone = clientPhone,
                    onClientPhoneChange = { clientPhone = it }
                )
            }
            
            // Tipo de servicio
            item {
                ServiceTypeSection(
                    selectedServiceType = selectedServiceType,
                    onServiceTypeClick = { showServiceTypeDialog = true }
                )
            }
            
            // Descripción del servicio
            item {
                ServiceDescriptionSection(
                    description = serviceDescription,
                    onDescriptionChange = { serviceDescription = it }
                )
            }
            
            // Costos
            item {
                CostsSection(
                    laborCost = laborCost,
                    onLaborCostChange = { laborCost = it },
                    materials = materials,
                    onMaterialsClick = { showMaterialsDialog = true },
                    additionalCosts = additionalCosts,
                    onAdditionalCostsClick = { showAdditionalCostsDialog = true }
                )
            }
            
            // Fecha de validez
            item {
                ValiditySection(
                    validUntil = validUntil,
                    onDateClick = { showDatePicker = true }
                )
            }
            
            // Resumen de la cotización
            item {
                QuoteSummarySection(
                    clientName = clientName,
                    serviceType = selectedServiceType,
                    description = serviceDescription,
                    laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                    materials = materials,
                    additionalCosts = additionalCosts
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            // Mostrar error si existe
            error?.let { errorMessage ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = StatusPending.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Error,
                                contentDescription = null,
                                tint = StatusPending
                            )
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = StatusPending
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Diálogos
    if (showServiceTypeDialog) {
        ServiceTypeDialog(
            selectedType = selectedServiceType,
            onTypeSelected = { 
                selectedServiceType = it
                showServiceTypeDialog = false
            },
            onDismiss = { showServiceTypeDialog = false }
        )
    }
    
    if (showMaterialsDialog) {
        MaterialsDialog(
            materials = materials,
            onMaterialsChanged = { materials = it },
            onDismiss = { showMaterialsDialog = false }
        )
    }
    
    if (showAdditionalCostsDialog) {
        AdditionalCostsDialog(
            additionalCosts = additionalCosts,
            onAdditionalCostsChanged = { additionalCosts = it },
            onDismiss = { showAdditionalCostsDialog = false }
        )
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {
                Text("Fecha de validez", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Selecciona hasta cuándo será válida la cotización")
                // Aquí puedes agregar el DatePicker real si lo necesitas
            }
        }
    }
}

@Composable
fun CreateQuoteHeader(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isLoading: Boolean
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TextPrimary
                )
            }
            
            Text(
                text = "Crear Cotización",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            TextButton(
                onClick = onSaveClick,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isLoading) TextSecondary else DeepSkyBlue
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = TextSecondary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ClientInformationSection(
    clientName: String,
    onClientNameChange: (String) -> Unit,
    clientAddress: String,
    onClientAddressChange: (String) -> Unit,
    clientPhone: String,
    onClientPhoneChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Información del Cliente",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            OutlinedTextField(
                value = clientName,
                onValueChange = onClientNameChange,
                label = { Text("Nombre del cliente") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )
            
            OutlinedTextField(
                value = clientAddress,
                onValueChange = onClientAddressChange,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )
            
            OutlinedTextField(
                value = clientPhone,
                onValueChange = onClientPhoneChange,
                label = { Text("Teléfono") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun ServiceTypeSection(
    selectedServiceType: ServiceType?,
    onServiceTypeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Tipo de Servicio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            OutlinedButton(
                onClick = onServiceTypeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (selectedServiceType != null) DeepSkyBlue else TextSecondary
                )
            ) {
                Icon(
                    imageVector = getServiceIcon(selectedServiceType ?: ServiceType.CLEANING),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedServiceType?.let { getServiceTypeName(it) } ?: "Seleccionar tipo de servicio"
                )
            }
        }
    }
}

@Composable
fun ServiceDescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Descripción del Servicio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Describe el servicio a realizar") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun CostsSection(
    laborCost: String,
    onLaborCostChange: (String) -> Unit,
    materials: List<Material>,
    onMaterialsClick: () -> Unit,
    additionalCosts: List<AdditionalCost>,
    onAdditionalCostsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Costos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            OutlinedTextField(
                value = laborCost,
                onValueChange = onLaborCostChange,
                label = { Text("Costo de mano de obra") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepSkyBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )
            
            OutlinedButton(
                onClick = onMaterialsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Icon(
                    Icons.Outlined.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Materiales (${materials.size})")
            }
            
            OutlinedButton(
                onClick = onAdditionalCostsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Costos adicionales (${additionalCosts.size})")
            }
        }
    }
}

@Composable
fun ValiditySection(
    validUntil: LocalDate,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Validez de la Cotización",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            OutlinedButton(
                onClick = onDateClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Válida hasta: ${validUntil.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                )
            }
        }
    }
}

@Composable
fun QuoteSummarySection(
    clientName: String,
    serviceType: ServiceType?,
    description: String,
    laborCost: Double,
    materials: List<Material>,
    additionalCosts: List<AdditionalCost>
) {
    val materialsTotal = materials.sumOf { it.total }
    val additionalCostsTotal = additionalCosts.sumOf { it.amount }
    val totalCost = laborCost + materialsTotal + additionalCostsTotal
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSecondary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen de la Cotización",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            
            if (clientName.isNotEmpty()) {
                Text(
                    text = "Cliente: $clientName",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary
                    )
                )
            }
            
            if (serviceType != null) {
                Text(
                    text = "Servicio: ${getServiceTypeName(serviceType)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary
                    )
                )
            }
            
            if (description.isNotEmpty()) {
                Text(
                    text = "Descripción: $description",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
            }
            
            Divider(color = TextSecondary.copy(alpha = 0.3f))
            
            Text(
                text = "Desglose de costos:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            
            Text(
                text = "Mano de obra: $${String.format("%.2f", laborCost)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
            
            Text(
                text = "Materiales: $${String.format("%.2f", materialsTotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
            
            Text(
                text = "Costos adicionales: $${String.format("%.2f", additionalCostsTotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
            
            Divider(color = TextSecondary.copy(alpha = 0.3f))
            
            Text(
                text = "Total: $${String.format("%.2f", totalCost)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepSkyBlue
                )
            )
        }
    }
}

@Composable
fun ServiceTypeDialog(
    selectedType: ServiceType?,
    onTypeSelected: (ServiceType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Tipo de Servicio",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceType.values().forEach { serviceType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTypeSelected(serviceType) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = getServiceIcon(serviceType),
                            contentDescription = null,
                            tint = getServiceTypeColor(serviceType),
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Text(
                            text = getServiceTypeName(serviceType),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (selectedType == serviceType) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = DeepSkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Text("Cerrar")
            }
        },
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun MaterialsDialog(
    materials: List<Material>,
    onMaterialsChanged: (List<Material>) -> Unit,
    onDismiss: () -> Unit
) {
    var materialName by remember { mutableStateOf("") }
    var materialQuantity by remember { mutableStateOf("") }
    var materialPrice by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Materiales",
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
                // Lista de materiales existentes
                if (materials.isNotEmpty()) {
                    Text(
                        text = "Materiales agregados:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    
                    materials.forEachIndexed { index, material ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = material.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Cantidad: ${material.quantity} - Precio: $${String.format("%.2f", material.total)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary
                                        )
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        val newMaterials = materials.toMutableList()
                                        newMaterials.removeAt(index)
                                        onMaterialsChanged(newMaterials)
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Eliminar",
                                        tint = StatusPending
                                    )
                                }
                            }
                        }
                    }
                }
                
                Divider(color = TextSecondary.copy(alpha = 0.3f))
                
                // Formulario para agregar nuevo material
                Text(
                    text = "Agregar material:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                OutlinedTextField(
                    value = materialName,
                    onValueChange = { materialName = it },
                    label = { Text("Nombre del material") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepSkyBlue,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = materialQuantity,
                        onValueChange = { materialQuantity = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepSkyBlue,
                            unfocusedBorderColor = TextSecondary
                        )
                    )
                    
                    OutlinedTextField(
                        value = materialPrice,
                        onValueChange = { materialPrice = it },
                        label = { Text("Precio unitario") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepSkyBlue,
                            unfocusedBorderColor = TextSecondary
                        )
                    )
                }
                
                Button(
                    onClick = {
                        val quantity = materialQuantity.toIntOrNull() ?: 0
                        val price = materialPrice.toDoubleOrNull() ?: 0.0
                        val total = quantity * price
                        
                        if (materialName.isNotEmpty() && quantity > 0 && price > 0) {
                            val newMaterial = Material(
                                name = materialName,
                                quantity = quantity,
                                unitPrice = price,
                                total = total
                            )
                            onMaterialsChanged(materials + newMaterial)
                            materialName = ""
                            materialQuantity = ""
                            materialPrice = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepSkyBlue
                    )
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Material")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Text("Cerrar")
            }
        },
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AdditionalCostsDialog(
    additionalCosts: List<AdditionalCost>,
    onAdditionalCostsChanged: (List<AdditionalCost>) -> Unit,
    onDismiss: () -> Unit
) {
    var costDescription by remember { mutableStateOf("") }
    var costAmount by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Costos Adicionales",
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
                // Lista de costos adicionales existentes
                if (additionalCosts.isNotEmpty()) {
                    Text(
                        text = "Costos adicionales:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    
                    additionalCosts.forEachIndexed { index, cost ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cost.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", cost.amount)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary
                                        )
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        val newCosts = additionalCosts.toMutableList()
                                        newCosts.removeAt(index)
                                        onAdditionalCostsChanged(newCosts)
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Eliminar",
                                        tint = StatusPending
                                    )
                                }
                            }
                        }
                    }
                }
                
                Divider(color = TextSecondary.copy(alpha = 0.3f))
                
                // Formulario para agregar nuevo costo
                Text(
                    text = "Agregar costo adicional:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                
                OutlinedTextField(
                    value = costDescription,
                    onValueChange = { costDescription = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepSkyBlue,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                
                OutlinedTextField(
                    value = costAmount,
                    onValueChange = { costAmount = it },
                    label = { Text("Monto") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepSkyBlue,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                
                Button(
                    onClick = {
                        val amount = costAmount.toDoubleOrNull() ?: 0.0
                        
                        if (costDescription.isNotEmpty() && amount > 0) {
                            val newCost = AdditionalCost(
                                description = costDescription,
                                amount = amount
                            )
                            onAdditionalCostsChanged(additionalCosts + newCost)
                            costDescription = ""
                            costAmount = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepSkyBlue
                    )
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Costo")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = DeepSkyBlue
                )
            ) {
                Text("Cerrar")
            }
        },
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(20.dp)
    )
}

// Funciones auxiliares (importadas desde ServiceModels.kt)
private fun getServiceTypeColor(type: ServiceType): Color {
    return when (type) {
        ServiceType.KITCHEN -> Color(0xFFFF6B35)
        ServiceType.DRAINAGE -> Color(0xFF4ECDC4)
        ServiceType.MAINTENANCE -> Color(0xFF45B7D1)
        ServiceType.CLEANING -> Color(0xFF96CEB4)
    }
}

private fun getServiceIcon(type: ServiceType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        ServiceType.KITCHEN -> Icons.Outlined.Restaurant
        ServiceType.DRAINAGE -> Icons.Outlined.WaterDrop
        ServiceType.MAINTENANCE -> Icons.Outlined.Build
        ServiceType.CLEANING -> Icons.Outlined.CleaningServices
    }
}

private fun getServiceTypeName(type: ServiceType): String {
    return when (type) {
        ServiceType.KITCHEN -> "Cocinas"
        ServiceType.DRAINAGE -> "Desazolve"
        ServiceType.MAINTENANCE -> "Mantenimiento"
        ServiceType.CLEANING -> "Limpieza"
    }
} 
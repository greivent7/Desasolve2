package com.isra2.desasolve2.ui.screens.worker

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
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
import com.isra2.desasolve2.navigation.Screen
import com.isra2.desasolve2.models.ServiceType
import com.isra2.desasolve2.models.Worker
import com.isra2.desasolve2.models.Assignment
import com.isra2.desasolve2.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(navController: NavController) {
    // Lista inicial de trabajadores
    val initialWorkers = listOf(
        "Jose Erasmo Sanchez",
        "Abner Laureano",
        "Jorge Torres",
        "César Lara Medina"
    )
    var workers by remember { mutableStateOf(initialWorkers) }
    var attendance by remember { mutableStateOf(workers.associateWith { false }) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var newWorkerName by remember { mutableStateOf(TextFieldValue("")) }
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    // Actualiza asistencia si cambia la lista de trabajadores
    LaunchedEffect(workers) {
        attendance = workers.associateWith { attendance[it] ?: false }
    }

    Scaffold(
        topBar = {
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
                    Text(
                        text = "Personal",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                DeepSkyBlue.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar trabajador", tint = DeepSkyBlue)
                    }
                }
            }
        },
        floatingActionButton = {
            if (hasChanges) {
                FloatingActionButton(
                    onClick = { showSaveConfirmation = true },
                    containerColor = DeepSkyBlue,
                    contentColor = PureWhite,
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Guardar cambios")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Administradora
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = DeepSkyBlue, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Maria Flores",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Administradora",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
            // Lista de trabajadores
            Text(
                text = "Trabajadores",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(workers) { worker ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = attendance[worker] == true,
                                onCheckedChange = {
                                    attendance = attendance.toMutableMap().apply { put(worker, it) }
                                    hasChanges = true
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DeepSkyBlue,
                                    uncheckedColor = MediumGray
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = worker,
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { showDeleteDialog = worker to true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar trabajador", tint = StatusPending)
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para agregar trabajador
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Agregar trabajador") },
            text = {
                OutlinedTextField(
                    value = newWorkerName,
                    onValueChange = { newWorkerName = it },
                    label = { Text("Nombre completo") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newWorkerName.text.trim()
                        if (name.isNotEmpty() && !workers.contains(name)) {
                            workers = workers + name
                            attendance = attendance + (name to false)
                            hasChanges = true
                        }
                        newWorkerName = TextFieldValue("")
                        showAddDialog = false
                    }
                ) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para eliminar trabajador
    showDeleteDialog?.let { (worker, show) ->
        if (show) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Eliminar trabajador") },
                text = { Text("¿Estás seguro de que deseas eliminar a $worker?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            workers = workers - worker
                            attendance = attendance - worker
                            hasChanges = true
                            showDeleteDialog = null
                        }
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") }
                }
            )
        }
    }

    // Diálogo de confirmación de guardado
    if (showSaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmation = false },
            title = { Text("Cambios guardados") },
            text = { Text("La asistencia y la lista de trabajadores han sido actualizadas.") },
            confirmButton = {
                TextButton(onClick = {
                    showSaveConfirmation = false
                    hasChanges = false
                }) { Text("OK") }
            }
        )
    }
}
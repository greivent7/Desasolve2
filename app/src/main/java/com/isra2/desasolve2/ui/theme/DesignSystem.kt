package com.isra2.desasolve2.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Sistema de Diseño Desasolve2
 * 
 * Este archivo contiene las constantes y componentes base para mantener
 * consistencia visual en toda la aplicación.
 */

// ============================================================================
// ESPACIADO Y DIMENSIONES
// ============================================================================

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
}

object Elevation {
    val none = 0.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 12.dp
    val xxl = 16.dp
}

object BorderRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val circle = 50
}

object IconSize {
    val sm = 16.dp
    val md = 20.dp
    val lg = 24.dp
    val xl = 28.dp
    val xxl = 32.dp
}

// ============================================================================
// COMPONENTES BASE
// ============================================================================

/**
 * Card moderna con elevación y bordes redondeados
 */
@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(BorderRadius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm)
    ) {
        content()
    }
}

/**
 * Card destacada para elementos importantes
 */
@Composable
fun HighlightedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(BorderRadius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.md)
    ) {
        content()
    }
}

/**
 * Contenedor con padding estándar
 */
@Composable
fun StandardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.padding(Spacing.lg)
    ) {
        content()
    }
}

/**
 * Sección con título y contenido
 */
@Composable
fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            ),
            modifier = Modifier.padding(bottom = Spacing.md)
        )
        content()
    }
}

// ============================================================================
// ESTADOS Y COLORES DE ESTADO
// ============================================================================

/**
 * Obtiene el color apropiado para un estado de servicio
 */
fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "COMPLETED", "COMPLETADO" -> StatusCompleted
        "IN_PROGRESS", "EN_PROGRESO" -> StatusInProgress
        "PENDING", "PENDIENTE" -> StatusPending
        "SCHEDULED", "PROGRAMADO" -> StatusScheduled
        else -> TextTertiary
    }
}

/**
 * Obtiene el color de fondo para un estado
 */
fun getStatusBackgroundColor(status: String): Color {
    return when (status.uppercase()) {
        "COMPLETED", "COMPLETADO" -> StatusCompleted.copy(alpha = 0.1f)
        "IN_PROGRESS", "EN_PROGRESO" -> StatusInProgress.copy(alpha = 0.1f)
        "PENDING", "PENDIENTE" -> StatusPending.copy(alpha = 0.1f)
        "SCHEDULED", "PROGRAMADO" -> StatusScheduled.copy(alpha = 0.1f)
        else -> TextTertiary.copy(alpha = 0.1f)
    }
}

// ============================================================================
// LAYOUTS Y ESTRUCTURAS
// ============================================================================

/**
 * Layout estándar para pantallas principales
 */
@Composable
fun StandardScreenLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        content()
    }
}

/**
 * Layout para listas con espaciado consistente
 */
@Composable
fun ListLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        content()
    }
}

// ============================================================================
// ANIMACIONES Y TRANSICIONES
// ============================================================================

object AnimationDuration {
    val fast = 150
    val normal = 300
    val slow = 500
}

object AnimationEasing {
    val easeInOut = androidx.compose.animation.core.EaseInOut
    val easeOut = androidx.compose.animation.core.EaseOut
    val easeIn = androidx.compose.animation.core.EaseIn
}

// ============================================================================
// CONSTANTES DE DISEÑO
// ============================================================================

object DesignConstants {
    // Altura mínima para elementos interactivos
    val minTouchTarget = 48.dp
    
    // Padding estándar para contenido
    val contentPadding = Spacing.lg
    
    // Margen estándar entre elementos
    val elementSpacing = Spacing.md
    
    // Radio de borde estándar
    val standardBorderRadius = BorderRadius.md
    
    // Elevación estándar para cards
    val standardElevation = Elevation.sm
} 
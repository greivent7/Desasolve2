package com.isra2.desasolve2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores claro moderno
private val ModernLightColorScheme = lightColorScheme(
    primary = InteractivePrimary,
    onPrimary = PureWhite,
    secondary = InteractiveSecondary,
    onSecondary = PureWhite,
    tertiary = StatusScheduled,
    onTertiary = PureWhite,
    
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = SurfacePrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,
    
    error = ErrorRed,
    onError = PureWhite,
    
    outline = CardBorder,
    outlineVariant = CardBorder,
    
    scrim = Color(0x80000000),
    surfaceTint = InteractivePrimary,
    
    // Colores personalizados para la aplicación
    surfaceBright = SurfaceTertiary,
    surfaceDim = SurfaceSecondary,
    surfaceContainer = SurfaceSecondary,
    surfaceContainerHigh = SurfaceTertiary,
    surfaceContainerHighest = CardBorder,
    surfaceContainerLow = BackgroundSecondary,
    surfaceContainerLowest = BackgroundPrimary
)

// Esquema de colores oscuro moderno
private val ModernDarkColorScheme = darkColorScheme(
    primary = InteractivePrimary,
    onPrimary = PureWhite,
    secondary = InteractiveSecondary,
    onSecondary = PureWhite,
    tertiary = StatusScheduled,
    onTertiary = PureWhite,
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFBDBDBD),
    
    error = ErrorRed,
    onError = PureWhite,
    
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161),
    
    scrim = Color(0x80000000),
    surfaceTint = InteractivePrimary,
    
    // Colores personalizados para la aplicación
    surfaceBright = Color(0xFF2D2D2D),
    surfaceDim = Color(0xFF1E1E1E),
    surfaceContainer = Color(0xFF2D2D2D),
    surfaceContainerHigh = Color(0xFF424242),
    surfaceContainerHighest = Color(0xFF616161),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainerLowest = Color(0xFF121212)
)

@Composable
fun Desasolve2Theme(
    darkTheme: Boolean = false, // Cambiar a tema claro por defecto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Configurar barra de estado con color del tema
            window.statusBarColor = colorScheme.background.toArgb()
            // Configurar barra de navegación
            window.navigationBarColor = colorScheme.background.toArgb()
            // Configurar iconos de la barra de estado según el tema
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
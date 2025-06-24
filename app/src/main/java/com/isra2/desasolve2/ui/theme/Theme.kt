package com.isra2.desasolve2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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

// Esquema de colores minimalista negro
private val MinimalistDarkColorScheme = darkColorScheme(
    primary = DeepSkyBlue,
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
    surfaceTint = DeepSkyBlue,
    
    // Colores personalizados para la aplicación
    surfaceBright = SurfaceTertiary,
    surfaceDim = SurfaceSecondary,
    surfaceContainer = SurfaceSecondary,
    surfaceContainerHigh = SurfaceTertiary,
    surfaceContainerHighest = CardBorder,
    surfaceContainerLow = Color(0xFF0F0F0F),
    surfaceContainerLowest = PureWhite
)

@Composable
fun Desasolve2Theme(
    darkTheme: Boolean = true, // Forzar tema oscuro por defecto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MinimalistDarkColorScheme
        else -> MinimalistDarkColorScheme // Mantener tema oscuro incluso en modo claro
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Configurar barra de estado con color negro absoluto
            window.statusBarColor = PureWhite.toArgb()
            // Configurar barra de navegación
            window.navigationBarColor = PureWhite.toArgb()
            // Configurar iconos de la barra de estado como claros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
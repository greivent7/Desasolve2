package com.isra2.desasolve2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.isra2.desasolve2.navigation.AppNavigation
import com.isra2.desasolve2.ui.theme.Desasolve2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d("MainActivity", "Iniciando aplicación Desasolve2")
            
            setContent {
                Desasolve2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
            
            Log.d("MainActivity", "Aplicación iniciada correctamente")
            
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al iniciar la aplicación", e)
            // Mostrar un mensaje de error simple
            setContent {
                Desasolve2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Contenido de error simple
                        androidx.compose.material3.Text(
                            text = "Error al cargar la aplicación. Por favor, reinicia la app.",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
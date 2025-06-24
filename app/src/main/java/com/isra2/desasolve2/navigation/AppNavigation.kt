package com.isra2.desasolve2.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import com.isra2.desasolve2.ui.screens.schedule.ScheduleScreen
import com.isra2.desasolve2.ui.screens.servicerequest.ServiceRequestScreen
import com.isra2.desasolve2.ui.screens.worker.WorkerDashboardScreen
import com.isra2.desasolve2.ui.screens.notifications.NotificationsScreen
import com.isra2.desasolve2.ui.screens.servicedetails.ServiceDetailsScreen
import com.isra2.desasolve2.ui.screens.photos.BeforeAfterPhotosScreen
import com.isra2.desasolve2.ui.theme.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { ModernBottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Schedule.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Schedule.route) {
                ScheduleScreen(navController)
            }
            composable(Screen.ServiceRequest.route) {
                ServiceRequestScreen(navController)
            }
            composable(Screen.WorkerDashboard.route) {
                WorkerDashboardScreen(navController)
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(navController)
            }
            composable(
                route = Screen.ServiceDetails.route
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId")
                ServiceDetailsScreen(serviceId = serviceId ?: "", navController = navController)
            }
            composable(
                route = Screen.BeforeAfterPhotos.route
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId")
                BeforeAfterPhotosScreen(serviceId = serviceId ?: "", navController = navController)
            }
        }
    }
}

@Composable
fun ModernBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavigationItem(
            route = Screen.Schedule.route,
            icon = Icons.Outlined.CalendarMonth,
            selectedIcon = Icons.Filled.CalendarMonth,
            label = "Calendario"
        ),
        NavigationItem(
            route = Screen.ServiceRequest.route,
            icon = Icons.Outlined.CleaningServices,
            selectedIcon = Icons.Filled.CleaningServices,
            label = "Cotizaciones"
        ),
        NavigationItem(
            route = Screen.WorkerDashboard.route,
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            label = "Personal"
        ),
        NavigationItem(
            route = Screen.Notifications.route,
            icon = Icons.Outlined.Notifications,
            selectedIcon = Icons.Filled.Notifications,
            label = "Avisos"
        )
    )

    NavigationBar(
        containerColor = NavigationBackground,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { 
                    Icon(
                        if (isSelected) item.selectedIcon else item.icon, 
                        contentDescription = item.label,
                        tint = if (isSelected) 
                            NavigationSelected 
                        else 
                            NavigationUnselected
                    ) 
                },
                label = { 
                    Text(
                        item.label,
                        color = if (isSelected) 
                            NavigationSelected 
                        else 
                            NavigationUnselected,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold 
                            else androidx.compose.ui.text.font.FontWeight.Medium
                        )
                    ) 
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavigationSelected,
                    selectedTextColor = NavigationSelected,
                    unselectedIconColor = NavigationUnselected,
                    unselectedTextColor = NavigationUnselected,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
} 
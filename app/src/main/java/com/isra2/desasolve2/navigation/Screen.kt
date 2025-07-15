package com.isra2.desasolve2.navigation

sealed class Screen(val route: String) {
    object Schedule : Screen("schedule")
    object ServiceRequest : Screen("service_request")
    object CreateQuote : Screen("create_quote")
    object ServiceDetails : Screen("service_details/{serviceId}") {
        fun createRoute(serviceId: String) = "service_details/$serviceId"
    }
    object WorkerDashboard : Screen("worker_dashboard")
    object BeforeAfterPhotos : Screen("before_after_photos/{serviceId}") {
        fun createRoute(serviceId: String) = "before_after_photos/$serviceId"
    }
    object Notifications : Screen("notifications")
} 
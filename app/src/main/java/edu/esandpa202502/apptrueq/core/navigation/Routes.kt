package edu.esandpa202502.apptrueq.core.navigation

/**
 * Rutas principales de la aplicación TrueQ.
 * Centraliza todas las pantallas del flujo de navegación.
 */
sealed class Routes(val route: String) {
    // --- PANTALLAS BASE ---
    object Dashboard : Routes("dashboard")
    object Explore : Routes("explore")
    object Login : Routes("auth")
    object Profile : Routes("profile")

    // --- MÓDULO HU-03: OFERTAS Y NECESIDADES ---
    object Offer : Routes("offer")
    object Need : Routes("need")
    object OfferForm : Routes("offerForm")

    // --- DETALLES DE PUBLICACIÓN ---
    object PublicationDetail : Routes("publicationDetail/{id}") {
        fun createRoute(id: String) = "publicationDetail/$id"
    }

    // --- MÓDULO DE INTERCAMBIOS ---
    object TradeHistory : Routes("tradeHistory")

    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    object Proposals_received : Routes("proposalsReceived")

    // --- MÓDULO DE REPORTES ---
    object Report_user : Routes("report")

    // --- MÓDULO DE NOTIFICACIONES ---
    object Notifications : Routes("notifications")

    object NotificationDetail : Routes("notification_detail/{notificationId}") {
        fun createRoute(notificationId: String) = "notification_detail/$notificationId"
    }
}

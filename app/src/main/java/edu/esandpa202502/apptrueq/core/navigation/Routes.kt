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

    // --- DETALLES DE PUBLICACIÓN ---
    object PublicationDetail : Routes("publicationDetail/{publicationId}") {
        fun createRoute(publicationId: String) = "publicationDetail/$publicationId"
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

    // --- MÓDULO DE OFERTAS (HU-03) ---
    object Offer : Routes("offer") // Pantalla principal de Ofertas (tabs)
    object OfferForm : Routes("offerForm") // Pantalla de formulario de oferta
}

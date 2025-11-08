package edu.esandpa202502.apptrueq.core.navigation

sealed class Routes(val route: String) {
    // Pantallas que ya existían
    object Dashboard : Routes("dashboard")
    object Explore : Routes("explore")
    object Login : Routes("auth")
    object Profile : Routes("profile")

    object PublicationDetail : Routes("publicationDetail/{publicationId}") {
        fun createRoute(publicationId: String) = "publicationDetail/$publicationId"
    }

    // Pantallas que se crearon
    object TradeHistory : Routes("tradeHistory")

    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    object Proposals_received : Routes("proposalsReceived")

    object Report_user : Routes("report")

    object Notifications : Routes("notifications")

    object NotificationDetail : Routes("notification_detail/{notificationId}") {
        fun createRoute(notificationId: String) = "notification_detail/$notificationId"
    }
}
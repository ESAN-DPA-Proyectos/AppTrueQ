package edu.esandpa202502.apptrueq.core.navigation

sealed class Routes(val route: String) {
    // Rutas sin argumentos
    object Login : Routes("login")
    object Register : Routes("register")
    object ForgotPassword : Routes("forgot_password")
    object Logout : Routes("logout")
    object Dashboard : Routes("dashboard")
    object Explore : Routes("explore")
    object TradeHistory : Routes("trade_history")
    object ProposalsReceived : Routes("proposals_received") // Renombrado de OffersReceived
    object Notifications : Routes("notifications")


    object Offer : Routes("offer")
    object Need : Routes("need")
    object ProposalsHistory : Routes("proposals_history")

    // Rutas con argumentos
    object PublicationDetail : Routes("publication_detail/{id}") {
        fun createRoute(id: String) = "publication_detail/$id"
    }

    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    object NotificationDetail : Routes("notification_detail/{notificationId}/{referenceId}") {
        fun createRoute(notificationId: String, referenceId: String) =
            "notification_detail/$notificationId/$referenceId"
    }

    object ReportUser : Routes("report_user/{reportedUserId}/{referenceId}") {
        fun createRoute(reportedUserId: String, referenceId: String) =
            "report_user/$reportedUserId/$referenceId"
    }

    object  PublicationQR : Routes("publication_qr")
}

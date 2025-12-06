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
        fun createRoute(notificationId: String, referenceId: String) = "notification_detail/$notificationId/$referenceId"
    }


    // SOLUCIÓN DEFINITIVA: Se define el objeto ReportUser con su función createRoute,
    // que construye la URL con los parámetros necesarios.
    object ReportUser : Routes("report_user/{userId}?publicationId={publicationId}") {
        fun createRoute(userId: String, publicationId: String?): String {
            val route = "report_user/$userId"
            // Añade el publicationId como un parámetro de consulta opcional.
            return if (publicationId != null) "$route?publicationId=$publicationId" else route
        }
    }
}

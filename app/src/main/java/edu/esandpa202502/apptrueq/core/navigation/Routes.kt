package edu.esandpa202502.apptrueq.core.navigation

// Todas las rutas de la app se centralizan aquí.
sealed class Routes(val route: String) {

    // ---- RUTAS SIN ARGUMENTOS ----
    object Login : Routes("login")
    object Register : Routes("register")
    object ForgotPassword : Routes("forgot_password")
    object Logout : Routes("logout")
    object Dashboard : Routes("dashboard")
    object Explore : Routes("explore")
    object TradeHistory : Routes("trade_history")
    object ProposalsReceived : Routes("proposals_received")
    object Notifications : Routes("notifications")
    object Offer : Routes("offer")
    object Need : Routes("need")
    object ModerationPanel : Routes("moderation_panel")
    object Denuncias : Routes("denuncias")
    object PublicationQR : Routes("publication_qr")

    // ---- RUTAS CON ARGUMENTOS ----

    // HU-10: Reportar usuario / publicación
    object ReportUser :
        Routes("reportUsr_user/{publicationId}/{reportedUserId}/{reportedUserName}") {

        fun createRoute(
            publicationId: String,
            reportedUserId: String,
            reportedUserName: String
        ): String = "reportUsr_user/$publicationId/$reportedUserId/$reportedUserName"
    }

    // Detalle de publicación (Explore)
    object PublicationDetail : Routes("publication_detail/{id}") {
        fun createRoute(id: String) = "publication_detail/$id"
    }

    // Detalle de trueque (Historial)
    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    // Detalle de notificación
    object NotificationDetail :
        Routes("notification_detail/{notificationId}/{referenceId}") {

        fun createRoute(
            notificationId: String,
            referenceId: String
        ) = "notification_detail/$notificationId/$referenceId"
    }

    // Detalle de denuncia (HU-11)
    object DenunciaDetail : Routes("denuncia_detail/{reportId}") {
        fun createRoute(reportId: String) = "denuncia_detail/$reportId"
    }
}

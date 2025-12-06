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
    object ProposalsReceived : Routes("proposals_received")
    object Notifications : Routes("notifications")
    object Offer : Routes("offer")
    object Need : Routes("need")
    object ModerationPanel : Routes("moderation_panel")
    object Denuncias : Routes("denuncias")

    // ANTES:
    // object ReportUser : Routes("reportUsr_user")

    // AHORA: ruta con los 3 parámetros
    object ReportUser :
        Routes("reportUsr_user/{publicationId}/{reportedUserId}/{reportedUserName}") {
        fun createRoute(
            publicationId: String,
            reportedUserId: String,
            reportedUserName: String
        ) = "reportUsr_user/$publicationId/$reportedUserId/$reportedUserName"
    }

    // Rutas con argumentos
    object PublicationDetail : Routes("publication_detail/{id}") {
        fun createRoute(id: String) = "publication_detail/$id"
    }

    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    object NotificationDetail :
        Routes("notification_detail/{notificationId}/{referenceId}") {
        fun createRoute(notificationId: String, referenceId: String) =
            "notification_detail/$notificationId/$referenceId"
    }

    object DenunciaDetail : Routes("denuncia_detail/{reportId}") {
        fun createRoute(reportId: String) = "denuncia_detail/$reportId"
    }
}


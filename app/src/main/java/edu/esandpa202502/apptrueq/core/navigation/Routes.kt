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
    object ModerationPanel : Routes("moderation_panel")
    object Denuncias : Routes("denuncias")
    object ReportedPublications : Routes("reported_publications") // Añadido
    object Offer : Routes("offer")
    object Need : Routes("need")
    object PublicationQR : Routes("publication_qr")

    // Rutas con argumentos
    object PublicationDetail : Routes("publication_detail/{id}") {
        fun createRoute(id: String) = "publication_detail/$id"
    }

    object ReportUser : Routes("report_user/{publicationId}/{reportedUserId}/{reportedUserName}") {
        fun createRoute(publicationId: String, reportedUserId: String, reportedUserName: String) = 
            "report_user/$publicationId/$reportedUserId/$reportedUserName"
    }
    
    object TradeDetail : Routes("trade_detail/{tradeId}") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }

    object NotificationDetail : Routes("notification_detail/{notificationId}/{referenceId}") {
        fun createRoute(notificationId: String, referenceId: String) = "notification_detail/$notificationId/$referenceId"
    }

    object DenunciaDetail : Routes("denuncia_detail/{reportId}") {
        fun createRoute(reportId: String) = "denuncia_detail/$reportId"
    }
}

package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// --- Módulos principales ---
import edu.esandpa202502.apptrueq.auth.ui.ForgotPasswordScreen
import edu.esandpa202502.apptrueq.dashboard.ui.DashboardScreen
import edu.esandpa202502.apptrueq.auth.ui.LoginScreen
import edu.esandpa202502.apptrueq.auth.ui.Logout
import edu.esandpa202502.apptrueq.auth.ui.RegisterScreen
import edu.esandpa202502.apptrueq.explore.ui.ExploreScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationDetailScreen
import edu.esandpa202502.apptrueq.reportUsr.ui.ReportUserScreen

// --- Módulo HU-03: Ofertas y Necesidades ---
import edu.esandpa202502.apptrueq.offer.ui.OfferScreen
import edu.esandpa202502.apptrueq.need.ui.NeedScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route
    ) {
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(Routes.Logout.route) {
            Logout(navController = navController)
        }

        composable(Routes.Explore.route) {
            ExploreScreen(navController = navController)
        }

        composable(Routes.Offer.route) {
            OfferScreen()
        }

        composable(Routes.Need.route) {
            NeedScreen()
        }

        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("id") ?: ""
            PublicationDetailScreen(
                navController = navController,
                publicationId = publicationId
            )
        }

        composable(Routes.TradeHistory.route) {
            TradeHistoryScreen(navController = navController)
        }

        composable(Routes.ProposalsReceived.route) {
            ProposalsReceivedScreen(navController = navController)
        }

        composable(
            route = Routes.TradeDetail.route,
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tradeId = backStackEntry.arguments?.getString("tradeId") ?: ""
            TradeDetailScreen(
                tradeId = tradeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(
            route = Routes.NotificationDetail.route,
            arguments = listOf(
                navArgument("notificationId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("referenceId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            val referenceId = backStackEntry.arguments?.getString("referenceId") ?: ""
            NotificationDetailScreen(
                navController = navController,
                notificationId = notificationId,
                referenceId = referenceId
            )
        }

        // HU-10: Reporte de usuario, ruta con argumento obligatorio {userId}
//        composable(
//            route = "reportUsr_user/{userId}",
//            arguments = listOf(
//                navArgument("userId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val reportedUserId = backStackEntry.arguments?.getString("userId") ?: ""
//            ReportUserScreen(
//                navController = navController,
//                reportedUserId = reportedUserId
//            )
//        }



        // HU-10: Reporte de usuario desde el menú lateral (sin argumento obligatorio por ahora)
        composable(
            route = Routes.ReportUser.route
        ) {
            // Por ahora no se pasa un userId específico desde el Drawer
            ReportUserScreen(
                navController = navController,
                reportedUserId = ""
            )
        }
    }
}
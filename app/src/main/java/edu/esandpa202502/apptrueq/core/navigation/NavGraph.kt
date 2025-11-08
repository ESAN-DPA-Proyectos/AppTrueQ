package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// Imports de las nuevas pantallas
import edu.esandpa202502.apptrueq.dashboard.ui.DashboardScreen
import edu.esandpa202502.apptrueq.auth.ui.LoginScreen
// Imports que ya tenía
import edu.esandpa202502.apptrueq.explore.ui.ExploreScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferListScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferFormScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route // Mantenemos Dashboard como pantalla inicial
    ) {
        // --- PANTALLAS PRINCIPALES DEL PROYECTO --
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.Explore.route) {
            ExploreScreen(navController = navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Routes.Profile.route) {
            // ProfileScreen(navController = navController) // Asumiendo que existe un ProfileScreen
        }
        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("publicationId") ?: ""
            PublicationDetailScreen(publicationId = publicationId, navController = navController)
        }

        // --- PANTALLAS QUE HEMOS TRABAJADO ---
        composable(route = Routes.TradeHistory.route) {
            TradeHistoryScreen(navController = navController)
        }
        composable(Routes.Proposals_received.route) {
            ProposalsReceivedScreen()
        }
        composable(Routes.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        composable(Routes.Report_user.route) {
            ReportUserScreen()
        }

        // --- RUTAS DE DETALLE ---
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
        composable(
            route = Routes.NotificationDetail.route,
            arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
        ) {
            val notificationId = it.arguments?.getString("notificationId")
            NotificationDetailScreen(
                notificationId = notificationId,
                navController = navController
            )
        }

        // 🔹 HU-03: Módulo Offer (tu rama)
        composable("offers") {
            OfferListScreen(navController)
        }
        composable("offerForm") {
            OfferFormScreen(onBack = { navController.popBackStack() })
        }
    }
}

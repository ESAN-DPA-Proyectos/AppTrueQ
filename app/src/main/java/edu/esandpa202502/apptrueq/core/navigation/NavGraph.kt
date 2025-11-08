package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType

// --- Pantallas principales y módulos ---
import edu.esandpa202502.apptrueq.dashboard.ui.DashboardScreen
import edu.esandpa202502.apptrueq.auth.ui.LoginScreen
import edu.esandpa202502.apptrueq.explore.ui.ExploreScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationDetailScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferListScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferFormScreen

/**
 * Gráfico de navegación principal de la aplicación TrueQ.
 * HU integradas: HU-03 (Ofertas), HU-08 (Notificaciones), HU-12 (Reportes), etc.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route // Pantalla inicial: Dashboard
    ) {
        // --- PANTALLAS PRINCIPALES ---
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
            // ProfileScreen(navController = navController)
        }

        // --- DETALLE DE PUBLICACIONES ---
        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("publicationId") ?: ""
            PublicationDetailScreen(
                publicationId = publicationId,
                navController = navController
            )
        }

        // --- MÓDULO EXCHANGE ---
        composable(route = Routes.TradeHistory.route) {
            TradeHistoryScreen(navController = navController)
        }

        composable(route = Routes.Proposals_received.route) {
            ProposalsReceivedScreen()
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

        // --- MÓDULO NOTIFICATIONS ---
        composable(Routes.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(
            route = Routes.NotificationDetail.route,
            arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            NotificationDetailScreen(
                notificationId = notificationId,
                navController = navController
            )
        }

        // --- MÓDULO REPORT ---
        composable(Routes.Report_user.route) {
            ReportUserScreen()
        }

        // --- MÓDULO OFFER (HU-03) ---
        composable(Routes.Offers.route) {
            OfferListScreen(navController)
        }

        composable(Routes.OfferForm.route) {
            OfferFormScreen(onBack = { navController.popBackStack() })
        }
    }
}

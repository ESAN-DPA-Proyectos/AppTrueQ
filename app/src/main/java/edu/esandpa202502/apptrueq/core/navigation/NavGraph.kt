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

// --- Módulo de Ofertas (HU-03) ---
import edu.esandpa202502.apptrueq.offer.ui.OfferScreen

/**
 * Gráfico de navegación principal de la aplicación TrueQ.
 * HU integradas:
 *  - HU-03 (Ofertas)
 *  - HU-08 (Notificaciones)
 *  - HU-12 (Reportes)
 *  - HU-13 (QR)
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route // Pantalla inicial
    ) {

        // --- DASHBOARD ---
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // --- AUTENTICACIÓN ---
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }

        // --- EXPLORAR PUBLICACIONES ---
        composable(Routes.Explore.route) {
            ExploreScreen(navController = navController)
        }

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

        // --- HISTORIAL DE INTERCAMBIOS ---
        composable(route = Routes.TradeHistory.route) {
            TradeHistoryScreen(navController = navController)
        }

        // --- PROPUESTAS RECIBIDAS ---
        composable(route = Routes.Proposals_received.route) {
            ProposalsReceivedScreen()
        }

        // --- DETALLE DE INTERCAMBIO ---
        composable(
            route = Routes.TradeDetail.route,
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tradeId = backStackEntry.arguments?.getString("tradeId") ?: ""
            TradeDetailScreen(tradeId = tradeId)
        }

        // --- NOTIFICACIONES ---
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

        // --- REPORTAR USUARIO ---
        composable(Routes.Report_user.route) {
            ReportUserScreen()
        }

        // --- MÓDULO HU-03: OFERTAS ---
        composable(Routes.Offer.route) {
            OfferScreen(vm = null) // si tu OfferScreen tiene un ViewModel interno, no pases nada
        }
    }
}

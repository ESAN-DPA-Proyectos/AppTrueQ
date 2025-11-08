package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import edu.esandpa202502.apptrueq.explore.ui.ExploreScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen

/**
 * Este es el gráfico de navegación principal de la aplicación.
 * Define todas las rutas o pantallas y las conecta con su Composable correspondiente.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Explore.route // 🧭 Pantalla inicial
    ) {
        // 🔹 Módulo Explore
        composable(Routes.Explore.route) {
            ExploreScreen(navController = navController)
        }
        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("publicationId") ?: ""
            PublicationDetailScreen(publicationId = publicationId, navController = navController)
        }

        // 🔹 Módulo Exchange
        composable(route = "trade_history") {
            TradeHistoryScreen(navController = navController)
        }
        composable(route = "proposals_received") {
            ProposalsReceivedScreen()
        }
        composable(
            route = "trade_detail/{tradeId}",
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tradeId = backStackEntry.arguments?.getString("tradeId") ?: ""
            TradeDetailScreen(
                tradeId = tradeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔹 Módulo Notifications
        composable(route = "notifications") {
            NotificationsScreen()
        }

        // 🔹 Módulo Report
        composable(route = "report_user") {
            ReportUserScreen()
        }
    }
}

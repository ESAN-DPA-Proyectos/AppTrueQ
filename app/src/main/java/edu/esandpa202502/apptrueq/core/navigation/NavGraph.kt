package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeDetailScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationDetailScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen

@Composable
fun NavGraph(navController: NavHostController) {
    
    // Cambié la ruta de inicio a "notifications" para que puedas probarla directamente.
    NavHost(navController = navController, startDestination = "notifications") {
        
        composable(route = "trade_history") {
            TradeHistoryScreen(navController = navController)
        }

        composable(route = "notifications") {
            // Le pasamos el NavController a la pantalla de notificaciones
            NotificationsScreen(navController = navController)
        }

        composable(route = "report_user") {
            ReportUserScreen()
        }
        
        composable(route = "proposals_received") {
            ProposalsReceivedScreen()
        }

        composable(
            route = "trade_detail/{tradeId}", 
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) {
            val tradeId = it.arguments?.getString("tradeId")
            
            TradeDetailScreen(
                tradeId = tradeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "notification_detail/{notificationId}",
            arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
        ) {
            val notificationId = it.arguments?.getString("notificationId")

            NotificationDetailScreen(
                notificationId = notificationId,
                navController = navController
            )
        }
    }
}

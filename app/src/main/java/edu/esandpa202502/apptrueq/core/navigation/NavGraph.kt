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
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen

@Composable
fun NavGraph(navController: NavHostController) {
    
    NavHost(navController = navController, startDestination = "trade_history") {
        
        composable(route = "trade_history") {
            TradeHistoryScreen(navController = navController)
        }

        composable(route = "notifications") {
            NotificationsScreen()
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
    }
}

package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen

/**
 * Este es el Gráfico de Navegación principal y único de la aplicación.
 * Define todas las posibles "rutas" o pantallas y las conecta con su Composable correspondiente.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    
    // NavHost es el contenedor que mostrará la pantalla actual según la ruta.
    // `startDestination` define qué pantalla se muestra primero al abrir la app.
    NavHost(navController = navController, startDestination = "trade_history") {
        
        composable(route = "trade_history") {
            TradeHistoryScreen()
        }

        composable(route = "notifications") {
            NotificationsScreen()
        }

        composable(route = "report_user") {
            ReportUserScreen()
        }
        
        // Ruta para la pantalla de propuestas recibidas, que habíamos perdido en el caos.
        composable(route = "proposals_received") {
            ProposalsReceivedScreen()
        }
    }
}

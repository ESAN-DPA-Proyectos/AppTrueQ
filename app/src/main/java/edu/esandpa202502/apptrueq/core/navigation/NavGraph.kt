package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.esandpa202502.apptrueq.exchange.ui.TradeHistoryScreen
import edu.esandpa202502.apptrueq.notification.ui.NotificationsScreen
import edu.esandpa202502.apptrueq.report.ui.ReportUserScreen

/**
 * Este es el Gráfico de Navegación principal de la aplicación.
 * Define todas las posibles "rutas" o pantallas y las conecta con su Composable correspondiente.
 * El NavController se pasa desde MainActivity para que todo el sistema de navegación esté conectado.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    
    // NavHost es el contenedor que mostrará la pantalla actual según la ruta.
    // `startDestination` define qué pantalla se muestra primero al abrir la app.
    NavHost(navController = navController, startDestination = "trade_history") {
        
        // Ruta para la pantalla del historial de trueques
        composable(route = "trade_history") {
            TradeHistoryScreen()
        }

        // Ruta para la pantalla de notificaciones
        composable(route = "notifications") {
            NotificationsScreen()
        }

        // Ruta para la pantalla de reportar usuario
        composable(route = "report_user") {
            ReportUserScreen()
        }
        
        // Aquí se podrían agregar más rutas en el futuro, como la de "Explorar", etc.
    }
}
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.esandpa202502.apptrueq.exchange.ui.ProposalsReceivedScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "proposals_received") {
        composable("proposals_received") {
            ProposalsReceivedScreen()
        }
    }
}

package edu.esandpa202502.apptrueq.core.navigation

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

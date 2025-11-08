package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.esandpa202502.apptrueq.offer.ui.OfferFormScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "offers"
    ) {
        composable("offers") {
            OfferListScreen(navController)
        }
        composable("offerForm") {
            OfferFormScreen(onBack = { navController.popBackStack() })
        }
    }
}

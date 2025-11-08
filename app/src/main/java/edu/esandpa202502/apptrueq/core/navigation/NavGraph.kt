package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import edu.esandpa202502.apptrueq.need.ui.NeedScreen
import edu.esandpa202502.apptrueq.offer.ui.OfferScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Offer.route // <-- Inicia con Offer
    ) {
        composable(Routes.Offer.route) {
            OfferScreen()
        }
        
        composable(Routes.Need.route) { // <-- Ruta corregida para Need
            NeedScreen()
        }

        // 🔧 Stub para que ExploreScreen pueda navegar sin romper la app
        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            PublicationDetailScreen(
                id = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

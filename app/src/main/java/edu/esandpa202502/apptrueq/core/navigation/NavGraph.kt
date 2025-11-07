package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.esandpa202502.apptrueq.explore.ui.ExploreScreen
import edu.esandpa202502.apptrueq.explore.ui.PublicationDetailScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Explore.route) {
        composable(Routes.Explore.route) {
            ExploreScreen(navController = navController)
        }
        composable(
            route = Routes.PublicationDetail.route,
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("publicationId") ?: ""
            PublicationDetailScreen(publicationId = publicationId)
        }
    }
}
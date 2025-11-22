package edu.esandpa202502.apptrueq.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.remote.firebase.FirebaseAuthManager

@Composable
fun Logout(navController: NavController) {
    LaunchedEffect(Unit) {
        FirebaseAuthManager.logout()
        navController.navigate(Routes.Login.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}

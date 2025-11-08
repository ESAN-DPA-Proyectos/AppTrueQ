package edu.esandpa202502.apptrueq.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido a AppTrueQ!",
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Este es tu Dashboard principal. Desde aquí puedes explorar todas las funcionalidades de la aplicación a través del menú lateral.",
                textAlign = TextAlign.Center
            )
            Button(onClick = { navController.navigate(Routes.Explore.route) }) {
                Text("Empezar a Explorar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    // Para la vista previa, necesitamos un NavController "falso"
     DashboardScreen(navController = NavController(LocalContext.current))
}

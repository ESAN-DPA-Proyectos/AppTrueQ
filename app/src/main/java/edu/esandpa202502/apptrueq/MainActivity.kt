package edu.esandpa202502.apptrueq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.esandpa202502.apptrueq.core.navigation.NavGraph
import edu.esandpa202502.apptrueq.ui.theme.AppTrueQTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTrueQTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Crear el controlador de navegación
                    val navController = rememberNavController()
                    // Llamar al gráfico de navegación
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

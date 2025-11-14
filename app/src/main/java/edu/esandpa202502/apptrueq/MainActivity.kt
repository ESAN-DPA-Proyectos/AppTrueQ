package edu.esandpa202502.apptrueq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.esandpa202502.apptrueq.core.navigation.DrawerScaffold
import edu.esandpa202502.apptrueq.core.navigation.NavGraph
import edu.esandpa202502.apptrueq.ui.theme.AppTrueQTheme

/**
 * Punto de entrada principal de la aplicación TrueQ.
 * Contiene el DrawerScaffold (menú lateral) y el gráfico de navegación general.
 * Integra las HU:
 *  - HU-03 (Ofertas)
 *  - HU-08 (Notificaciones)
 *  - HU-12 (Reportes)
 *  - HU-13 (QR)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTrueQTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Estructura principal con Drawer + NavGraph
                    DrawerScaffold(navController = navController) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

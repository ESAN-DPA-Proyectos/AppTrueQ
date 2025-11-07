package edu.esandpa202502.apptrueq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.esandpa202502.apptrueq.core.navigation.NavGraph
import edu.esandpa202502.apptrueq.ui.theme.AppTrueQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTrueQTheme {
                NavGraph()
            }
        }
    }
}

package edu.esandpa202502.apptrueq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import edu.esandpa202502.apptrueq.auth.ui.LoginScreen
import edu.esandpa202502.apptrueq.auth.ui.RegisterScreen
import edu.esandpa202502.apptrueq.ui.theme.AppTrueQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTrueQTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(Modifier.padding(innerPadding))
                    RegisterScreen(Modifier.padding(innerPadding))
                 }
            }
        }
    }
}



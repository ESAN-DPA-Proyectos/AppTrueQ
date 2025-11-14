package edu.esandpa202502.apptrueq.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes


@Composable
// 1. Añadimos el NavController como parámetro para poder navegar.
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge)

        // email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
        )

        //password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
        )
        // dar espacio entre el texto y el boton
        Spacer(modifier = Modifier.height(16.dp))

        // boton de iniciar sesion
        // 2. Implementamos la lógica de navegación.
        Button(onClick = {
            // TODO: Aquí iría la lógica de verificación de usuario y contraseña con Firebase.
            // Por ahora, simulamos un login exitoso.

            // Navegamos al Dashboard y limpiamos la pila de navegación.
            navController.navigate(Routes.Dashboard.route) {
                // Esto elimina todas las pantallas anteriores (incluyendo Login) del historial.
                popUpTo(0) { inclusive = true }
                // Asegura que no tengamos múltiples copias del Dashboard.
                launchSingleTop = true
            }
        }) {
            Text("Iniciar Sesión")
        }


    }

}

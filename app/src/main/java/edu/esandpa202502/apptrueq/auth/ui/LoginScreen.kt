package edu.esandpa202502.apptrueq.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.remote.firebase.FirebaseAuthManager
import edu.esandpa202502.apptrueq.ui.theme.AppTrueQTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
// 1. Añadimos el NavController como parámetro para poder navegar.
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){
    AppTrueQTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val context = LocalContext.current

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
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
            )
            // dar espacio entre el texto y el boton
            Spacer(modifier = Modifier.height(16.dp))

            // boton de iniciar sesion
            // 2. Implementamos la lógica de navegación.


            Button(
                onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = FirebaseAuthManager.loginUser(email, password)
                        if (result.isSuccess) {
                            navController.navigate(Routes.Dashboard.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))



            Button(
                onClick = { 
                navController.navigate(Routes.Register.route)
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿No tienes una cuenta? Regístrate")
            }
        }
    }
}
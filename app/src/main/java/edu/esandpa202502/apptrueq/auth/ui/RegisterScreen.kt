package edu.esandpa202502.apptrueq.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.remote.firebase.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController : NavController) {
    var name by remember  { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(
        modifier = Modifier.padding(16.dp)
    ){
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.titleLarge)

        // Campo de texto para el nombre de usuario
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
        )

        // Campo de texto para el correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
        )

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Para ocultar la contraseña
        )

        // Campo de texto para confirmar la contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Para ocultar la contraseña
        )

        // Botón para registrar al usuario
        Button(
            onClick = {
                if(name.isNotBlank()
                    && password.isNotBlank()
                    && password == confirmPassword) {

                    CoroutineScope(Dispatchers.Main).launch {

                        val result = FirebaseAuthManager.registerUser(name, email, password)
                        if(result.isSuccess){
                            navController.navigate(Routes.Login.route) {
                                popUpTo(Routes.Register.route) { inclusive = true }
                                launchSingleTop = true
                            }

                        }else{
                            val error = result.exceptionOrNull()?.message?: "Error desconocido"
                            Toast.makeText(context,
                                error,
                                Toast.LENGTH_LONG).show()
                        }
                    }


                }
            }
        ) {
            Text(text = "Registrar")
        }

    }

}
package edu.esandpa202502.apptrueq.report.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUserScreen() {

    var idDenunciado by remember { mutableStateOf("") }
    var nombreDenunciado by remember { mutableStateOf("Nombre del denunciado") }
    var motivoReporte by remember { mutableStateOf("Escoja motivo de reporte") }
    var descripcion by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val motivos = listOf("Fraude", "Incumplimiento", "Lenguaje ofensivo", "Spam")
    val maxCaracteresDescripcion = 250

    val limpiarCampos = {
        idDenunciado = ""
        nombreDenunciado = "Nombre del denunciado"
        motivoReporte = "Escoja motivo de reporte"
        descripcion = ""
        isMenuExpanded = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Reportar usuario") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Navegación hacia atrás */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = idDenunciado,
                            onValueChange = { idDenunciado = it },
                            label = { Text("ID del denunciado") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            nombreDenunciado = when (idDenunciado) {
                                "" -> "Nombre del denunciado"
                                "09909180" -> "Victor Leonardo"
                                "74585215" -> "Alex Loayza"
                                "09956545" -> "Marcello Motta"
                                else -> "Usuario no encontrado"
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Text(text = nombreDenunciado)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Los reportes seran revisados por un moderador y el mal uso puede llevar a bloquear su cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = isMenuExpanded,
                        onExpandedChange = { isMenuExpanded = !isMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = motivoReporte,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            motivos.forEach { motivo ->
                                DropdownMenuItem(
                                    text = { Text(motivo) },
                                    onClick = {
                                        motivoReporte = motivo
                                        isMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= maxCaracteresDescripcion) {
                                descripcion = it
                            }
                        },
                        label = { Text("Descripción") },
                        placeholder = { Text("Ingrese una breve descripcion del incidente. (mínimo 10 caracteres y máximo 250 caracteres)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        supportingText = { Text(text = "${descripcion.length} / $maxCaracteresDescripcion", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Cancelar")
                        }

                        Button(onClick = {
                            val isFormValid = idDenunciado.isNotEmpty() &&
                                    (nombreDenunciado != "Nombre del denunciado" && nombreDenunciado != "Usuario no encontrado") &&
                                    motivoReporte != "Escoja motivo de reporte" &&
                                    descripcion.length >= 10

                            scope.launch {
                                if (isFormValid) {
                                    snackbarHostState.showSnackbar("Reporte enviado correctamente")
                                    limpiarCampos()
                                } else {
                                    snackbarHostState.showSnackbar("Por favor, complete todos los campos requeridos.")
                                }
                            }
                        }) {
                            Text("Enviar")
                        }
                    }
                }
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Confirmación") },
                text = { Text("¿Realmente desea cancelar?\nSe perderán los datos ingresados.") },
                confirmButton = {
                    Button(
                        onClick = {
                            limpiarCampos()
                            showCancelDialog = false
                        }
                    ) {
                        Text("SI")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showCancelDialog = false }
                    ) {
                        Text("NO")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportUserScreenPreview() {
    ReportUserScreen()
}
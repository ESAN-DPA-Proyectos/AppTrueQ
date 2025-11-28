package edu.esandpa202502.apptrueq.report.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.report.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUserScreen(navController: NavController, reportViewModel: ReportViewModel = viewModel()) {

    // Estado del formulario
    var reportedEmail by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Escoja motivo de reporte") }
    var description by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    // Estado de la UI desde el ViewModel
    val uiState by reportViewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val motivos = listOf("Fraude", "Incumplimiento", "Lenguaje ofensivo", "Spam")

    fun clearForm() {
        reportedEmail = ""
        reason = "Escoja motivo de reporte"
        description = ""
        reportViewModel.resetState()
    }

    // Efecto para reaccionar a los cambios de estado del ViewModel
    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Reporte enviado correctamente.")
                clearForm()
            }
        }
        if (uiState.error != null) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: ${uiState.error}")
                reportViewModel.resetState()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Reportar Usuario") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
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
                    Text(
                        text = "Reporta a un usuario que ha incumplido las normas. Tu reporte es anónimo.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de texto para el correo electrónico
                    OutlinedTextField(
                        value = reportedEmail,
                        onValueChange = { reportedEmail = it },
                        label = { Text("Correo electrónico del denunciado") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Motivo del reporte
                    ExposedDropdownMenuBox(expanded = isMenuExpanded, onExpandedChange = { isMenuExpanded = !isMenuExpanded }) {
                        OutlinedTextField(
                            value = reason,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Motivo del reporte") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }) {
                            motivos.forEach { motivo ->
                                DropdownMenuItem(text = { Text(motivo) }, onClick = {
                                    reason = motivo
                                    isMenuExpanded = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción (mínimo 10 caracteres)") },
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                    } else {
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

                            Button(
                                onClick = {
                                    reportViewModel.submitReport(reportedEmail, reason, description)
                                },
                                // El botón se deshabilita si el formulario no es válido
                                enabled = reportedEmail.isNotBlank() && reason != "Escoja motivo de reporte" && description.length >= 10
                            ) {
                                Text("Enviar Reporte")
                            }
                        }
                    }
                }
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Confirmación") },
                text = { Text("¿Realmente desea cancelar? Se perderán los datos ingresados.") },
                confirmButton = {
                    Button(
                        onClick = {
                            clearForm()
                            showCancelDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCancelDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
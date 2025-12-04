package edu.esandpa202502.apptrueq.report.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla para HU-10: Reportar un usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUserScreen(
    navController: NavController,
    reportedUserId: String, // ID del usuario a reportar, recibido desde la navegación.
    viewModel: ReportViewModel = viewModel()
) {
    // --- ESTADOS DE LA UI ---
    var reason by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // MEJORA: Lista predefinida de motivos para el reporte.
    val reportReasons = listOf(
        "Contenido inapropiado",
        "Spam o publicidad no deseada",
        "Acoso o discurso de odio",
        "Información falsa",
        "Intento de estafa",
        "Otro (especificar en comentarios)"
    )

    // Efecto para manejar el resultado del envío del reporte.
    LaunchedEffect(viewModel.uiState) {
        viewModel.uiState.collectLatest { state ->
            if (state.reportSent) {
                snackbarHostState.showSnackbar("Reporte enviado correctamente. Gracias por tu colaboración.")
                navController.popBackStack()
            }
            if (state.error != null) {
                snackbarHostState.showSnackbar("Error: ${state.error}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Usuario") },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // MEJORA: Se reemplaza el TextField por un menú desplegable (ExposedDropdownMenuBox).
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = {}, // No se cambia directamente, se selecciona de la lista.
                    readOnly = true,
                    label = { Text("Motivo del reporte (obligatorio)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = uiState.error != null && reason.isBlank()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    reportReasons.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                reason = selectionOption // Actualiza el estado con la selección.
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Campo para comentarios adicionales.
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Comentarios adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón para enviar el reporte.
            Button(
                onClick = {
                    viewModel.submitReport(reportedUserId, reason, comments)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && reason.isNotBlank() // Se activa solo si hay un motivo seleccionado.
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar Reporte")
                }
            }
        }
    }
}
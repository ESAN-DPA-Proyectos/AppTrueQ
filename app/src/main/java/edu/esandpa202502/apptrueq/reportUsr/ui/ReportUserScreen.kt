package edu.esandpa202502.apptrueq.reportUsr.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.reportUsr.viewmodel.ReportState
import edu.esandpa202502.apptrueq.reportUsr.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUserScreen(
    navController: NavController,
    publicationId: String,
    reportedUserId: String,
    reportedUserName: String,
    viewModel: ReportViewModel = viewModel()
) {
    var reason by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    val reportState by viewModel.reportState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    val reportReasons = listOf(
        "Contenido inapropiado",
        "Spam o publicidad no deseada",
        "Acoso o discurso de odio",
        "Información falsa",
        "Intento de estafa",
        "Otro (especificar en comentarios)"
    )

    LaunchedEffect(reportState) {
        when (val state = reportState) {
            is ReportState.Success -> {
                snackbarHostState.showSnackbar("Reporte enviado correctamente. Gracias por tu colaboración.")
                navController.popBackStack()
            }
            is ReportState.Error -> {
                snackbarHostState.showSnackbar("Error: ${state.message}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar a $reportedUserName") },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Motivo del reporte (obligatorio)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = reportState is ReportState.Error && reason.isBlank()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        reportReasons.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    reason = selectionOption
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Comentarios adicionales (opcional)") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }

            Button(
                onClick = {
                    val reporterId = currentUser?.uid ?: ""
                    val reporterName = currentUser?.displayName ?: "Usuario Anónimo"
                    viewModel.submitReport(
                        publicationId = publicationId,
                        reportedUserId = reportedUserId,
                        reportedUserName = reportedUserName,
                        reason = reason,
                        description = comments,
                        reporterId = reporterId,
                        reporterName = reporterName // Añadido
                    )
                },
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                enabled = reportState !is ReportState.Loading && reason.isNotBlank()
            ) {
                if (reportState is ReportState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar Reporte")
                }
            }
        }
    }
}
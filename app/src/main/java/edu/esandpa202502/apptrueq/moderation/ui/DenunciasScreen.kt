package edu.esandpa202502.apptrueq.moderation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Report
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModel
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DenunciasScreen(
    navController: NavController, // Añadido NavController
    moderationViewModel: ModerationViewModel = viewModel(factory = ModerationViewModelFactory())
) {
    val reports by moderationViewModel.reports.collectAsState()
    DenunciasScreenContent(navController = navController, reports = reports)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DenunciasScreenContent(navController: NavController, reports: List<Report>) {
    var selectedStatus by remember { mutableStateOf("Todas") }

    val filteredReports = if (selectedStatus == "Todas") {
        reports
    } else {
        reports.filter { it.status == selectedStatus }
    }

    Scaffold(
        topBar = { /* TODO: Add TopAppBar */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Denuncias recibidas", style = MaterialTheme.typography.titleLarge)
            ReportFilter(
                selectedStatus = selectedStatus,
                onStatusSelected = { newStatus -> selectedStatus = newStatus }
            )
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(filteredReports, key = { it.id }) { report ->
                    ReportItem(navController = navController, report = report)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportFilter(selectedStatus: String, onStatusSelected: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("Todas", "Pendiente", "En revisión", "Resuelta")

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        TextField(
            value = selectedStatus,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(text = status) },
                    onClick = {
                        onStatusSelected(status)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReportItem(navController: NavController, report: Report) {
    val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "RPT${report.id.take(6).uppercase()}", fontWeight = FontWeight.Bold)
            Text(text = "Denunciado: ${report.reportedUserName}")
            Text(text = "Denunciante: ${report.reportingUserId}")
            Text(text = "Motivo: ${report.reason}")
            report.createdAt?.let {
                Text(text = "Fecha: ${dateFormatter.format(it.toDate())}")
            }
            Text(text = "Estado: ${report.status}")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { navController.navigate(Routes.PublicationDetail.createRoute(report.publicationId)) }) { // Corregido: Navega al detalle de la publicación
                    Text("Revisar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* TODO: Lógica de Resolver */ }) {
                    Text("Resolver")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DenunciasScreenPreview() {
    MaterialTheme {
        DenunciasScreenContent(navController = rememberNavController(), reports = emptyList())
    }
}

package edu.esandpa202502.apptrueq.moderation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.model.Report


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationPanelScreen(viewModel: ModerationViewModel = viewModel()) {
    val reports by viewModel.reports.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel de Moderación") })
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(reports) { report ->
                ReportItem(report = report)
            }
        }
    }
}

@Composable
fun ReportItem(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Usuario reportado: ${report.reportedUserName}")
            Text(text = "Motivo: ${report.reason}")
            Text(text = "Descripción: ${report.description}")
        }
    }
}

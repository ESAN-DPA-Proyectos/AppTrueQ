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
import androidx.compose.material3.ExposedDropdownMenu
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
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Report
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModel
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

object ReportStatus {
    const val PENDING = "Pendiente de revisión"
    const val IN_REVIEW = "En revisión"
    const val RESOLVED = "Resuelta"
}

@Composable
fun DenunciasScreen(
    navController: NavController,
    moderationViewModel: ModerationViewModel = viewModel(factory = ModerationViewModelFactory())
) {
    val reports by moderationViewModel.reports.collectAsState()
    DenunciasScreenContent(
        navController = navController,
        reports = reports,
        viewModel = moderationViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DenunciasScreenContent(
    navController: NavController,
    reports: List<Report>,
    viewModel: ModerationViewModel
) {
    var selectedStatus by remember { mutableStateOf("Todas") }

    val filteredReports = if (selectedStatus == "Todas") {
        reports
    } else {
        rep

package edu.esandpa202502.apptrueq.moderation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModel
import edu.esandpa202502.apptrueq.moderation.viewmodel.ModerationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationScreen(
    navController: NavController,
    moderationViewModel: ModerationViewModel = viewModel(factory = ModerationViewModelFactory())
) {
    val reports by moderationViewModel.reports.collectAsState()
    val pendingReportsCount by moderationViewModel.pendingReportsCount.collectAsState()
    val inReviewReportsCount by moderationViewModel.inReviewReportsCount.collectAsState()
    val resolvedReportsCount by moderationViewModel.resolvedReportsCount.collectAsState()

    Scaffold(
        topBar = { /* TODO: Add TopAppBar */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Panel Moderador",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ModerationCard(
                icon = Icons.Default.Report,
                title = "Denuncias",
                mainText = "${reports.size} Denuncias registradas",
                secondaryText = "$pendingReportsCount pendientes\n$inReviewReportsCount en revisión\n$resolvedReportsCount resueltas",
                onClick = { navController.navigate(Routes.Denuncias.route) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModerationCard(
                icon = Icons.Default.Assignment,
                title = "Publicaciones",
                mainText = "8 publicaciones revisadas",
                secondaryText = "3 reportadas\n2 bloqueadas",
                onClick = { navController.navigate(Routes.ReportedPublications.route) } // CORREGIDO
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModerationCard(
                icon = Icons.Default.People,
                title = "Usuarios",
                mainText = "15 usuarios gestionados",
                secondaryText = "6 denunciados\n2 suspendidos",
                onClick = { /* TODO: Navegar a Usuarios */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModerationCard(
                icon = Icons.Default.History,
                title = "Historial",
                mainText = "Registro de acciones",
                secondaryText = "8 recientes\nÚltimas 24h",
                onClick = { /* TODO: Navegar a Historial */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Resumen del día",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(count = pendingReportsCount, label = "denuncias pendientes")
                SummaryItem(count = 3, label = "Posts reportados")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModerationCard(
    icon: ImageVector,
    title: String,
    mainText: String,
    secondaryText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = mainText, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = secondaryText, fontSize = 12.sp, lineHeight = 14.sp)
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun SummaryItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun ModerationScreenPreview() {
    ModerationScreen(navController = rememberNavController())
}

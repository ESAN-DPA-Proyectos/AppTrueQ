package edu.esandpa202502.apptrueq.offer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferScreen(vm: OfferViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Publicar Oferta", "Mis Publicaciones")

    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Publicación Exitosa") },
            text = { Text("Tu oferta ha sido registrada correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        selectedTabIndex = 1 // Cambia a la pestaña de lista
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { 
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        ) 
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> OfferFormScreen(vm = vm, onSuccess = { showSuccessDialog = true })
            1 -> OfferListScreen(vm = vm)
        }
    }
}

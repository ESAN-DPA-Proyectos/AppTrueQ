package edu.esandpa202502.apptrueq.need.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.need.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedScreen(vm: NeedViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Publicar Necesidad", "Mis Necesidades")

    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Publicación Exitosa") },
            text = { Text("Tu necesidad ha sido registrada correctamente.") },
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
            0 -> NeedFormScreen(vm = vm, onSuccess = { showSuccessDialog = true })
            1 -> NeedListScreen(vm = vm)
        }
    }
}

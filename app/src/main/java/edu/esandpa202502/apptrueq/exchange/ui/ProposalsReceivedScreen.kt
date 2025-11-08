package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Proposal(
    val id: Int,
    val from: String,
    val offer: String,
    val message: String,
    val date: String,
    val status: String,
    val userImage: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ProposalsReceivedScreen() {
    var proposals by remember {
        mutableStateOf(listOf(
            Proposal(1, "Juan Rodriguez", "Ofrezco Laptop y Computadoras", "Te ofrezco Laptop Apple i9", "25/09/2025 -- 10:25 am", "Pendiente", android.R.drawable.ic_dialog_info),
            Proposal(2, "Marilyn Paira", "Libros de programación", "Necesito libro Phyton 3.07", "17/07/2025 -- 11:17 am", "Pendiente", android.R.drawable.ic_dialog_info)
        ))
    }
// N.º 1: La lista de propuestas ahora es un estado mutable.
// Esto permite que la UI reaccione cuando cambiamos el estado de una propuesta.
    var showDialog by remember { mutableStateOf(false) }
    var selectedProposalId by remember { mutableStateOf<Int?>(null) }
    var actionToConfirm by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. Añadimos el SnackbarHost al Scaffold
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Propuestas Recibidas") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back press */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(proposals) { proposal ->
                ProposalCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    proposal = proposal,
                    onAcceptClick = {
                        selectedProposalId = proposal.id
                        actionToConfirm = "accept"
                        showDialog = true
                    },
                    onRejectClick = {
                        selectedProposalId = proposal.id
                        actionToConfirm = "reject"
                        showDialog = true
                    }
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("¿Está seguro de esta acción?") },
                confirmButton = {
                    Button(onClick = {
                        // Lógica para actualizar el estado de la propuesta.
                        proposals = proposals.map {
                            if (it.id == selectedProposalId) {
                                it.copy(status = if (actionToConfirm == "accept") "Aceptado" else "Rechazado")
                            } else {
                                it
                            }
                        }


                        scope.launch {

                            val message = if (actionToConfirm == "accept") {
                                "Propuesta aceptada correctamente"
                            } else {
                                "Propuesta rechazada"
                            }
                            snackbarHostState.showSnackbar(message)
                        }

                        showDialog = false
                    }) {
                        Text("SI")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("NO")
                    }
                }
            )
        }
    }
}

@Composable
fun ProposalCard(
    proposal: Proposal,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = proposal.userImage),
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "De: ${proposal.from}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Oferta: \"${proposal.offer}\"")
            Text(text = "Mensaje: \"${proposal.message}\"")
            Text(text = "Fecha: ${proposal.date}")
            Text(text = "Estado: \"${proposal.status}\"")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val areButtonsEnabled = proposal.status == "Pendiente"

                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = areButtonsEnabled
                ) {
                    Text("Aceptar", color = Color.White)
                }
                Button(
                    onClick = onRejectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = areButtonsEnabled
                ) {
                    Text("Rechazar", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProposalsReceivedScreenPreview() {
    ProposalsReceivedScreen()
}

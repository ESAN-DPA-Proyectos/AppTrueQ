package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    val proposals = listOf(
        Proposal(1, "Juan Rodriguez", "Ofrezco Laptop y Computadoras", "Te ofrezco Laptop Apple i9", "25/09/2025 -- 10:25 am", "Pendiente", android.R.drawable.ic_dialog_info),
        Proposal(2, "Marilyn Paira", "Libros de programación", "Necesito libro Phyton 3.07", "17/07/2025 -- 11:17 am", "Pendiente", android.R.drawable.ic_dialog_info)
    )

    var showDialog by remember { mutableStateOf(false) }
    var selectedProposalId by remember { mutableStateOf<Int?>(null) }
    var actionToConfirm by remember { mutableStateOf<String?>(null) }

    Scaffold(
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(proposals) { proposal ->
                ProposalCard(
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("¿Está seguro de esta acción?") },
                confirmButton = {
                    Button(onClick = {
                        println("Action: ${actionToConfirm}, Proposal ID: ${selectedProposalId}")
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
fun ProposalCard(proposal: Proposal, onAcceptClick: () -> Unit, onRejectClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Button(onClick = onAcceptClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text("Aceptar", color = Color.White)
                }
                Button(onClick = onRejectClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text("Rechazar", color = Color.White)
                }
            }
        }
    }
}

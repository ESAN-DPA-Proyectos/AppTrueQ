package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Login : Routes("login", "Login", Icons.Default.Person)
    object Register : Routes("register", "Registro", Icons.Default.Person)
    object ForgotPassword : Routes("forgot_password", "Recuperar Contraseña", Icons.Default.Person)
    object Explore : Routes("explore", "Explorar", Icons.Default.Search)
    object Dashboard : Routes("dashboard", "Dashboard", Icons.Default.Home)
    object Favorites : Routes("favorites", "Favoritos", Icons.Default.Favorite)
    object MyPublications : Routes("my_publications", "Mis Publicaciones", Icons.Default.List) // NUEVA RUTA
    object CreatePublication : Routes("create_publication", "Crear Publicación", Icons.Default.AddCircle)
    object Profile : Routes("profile", "Mi Perfil", Icons.Default.Face)
    object Notifications : Routes("notifications", "Notificaciones", Icons.Default.Notifications)
    object ProposalsReceived : Routes("proposals_received", "Propuestas Recibidas", Icons.Default.Email)
    object ProposalsSent : Routes("proposals_sent", "Propuestas Enviadas", Icons.Default.CheckCircle)
    object TradeHistory : Routes("trade_history", "Historial de Trueques", Icons.Default.Home)
    // Ruta con argumento
    object PublicationDetail : Routes("publication_detail/{publicationId}", "Detalle", Icons.Default.List) {
        fun createRoute(publicationId: String) = "publication_detail/$publicationId"
    }
}
